<template>
    <!-- HEADER -->
    <ChatHeader :isShowInfoSection="isShowInfoSection"
        @update:isShowInfoSection="val => emit('update:isShowInfoSection', val)" />

    <!-- MESSAGES -->
    <div ref="scrollContainer" class="flex-1 overflow-y-auto p-6 space-y-2" @scroll="scrollMore">
        <div v-if="messageStorage.isLoading" class="text-center text-gray-400 text-sm">
            <LoadingSpinner />
        </div>

        <div v-for="msg in messagesWithMeta" :key="msg.id">
            <!-- TIME SEPARATOR -->
            <div v-if="msg.showTimeSeparator" class="text-center text-xs text-gray-400 my-3">
                {{ formatSeparatorTime(msg.ct) }}
            </div>

            <MessageContainer :message="msg" :friendProfileModal="friendProfileModal" />
        </div>
    </div>

    <Modal ref="friendProfileModal" :title="t('profile')">
        <friend-profile-u-i :user="getRecipient(conversationStorage.conversation!)" />
    </Modal>

    <div v-if="typingUsers.size > 0" class="text-sm text-gray-600 dark:text-gray-400 px-4 pb-2 flex items-center gap-1">
        <span>
            <span v-for="([id, user], index) in typingUsers" :key="id">
                <span v-if="index > 0">, </span>{{ user.username }}
            </span>
            {{ t("typing") }}
        </span>

        <!-- DOTS -->
        <span class="flex gap-1 ml-1">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
        </span>
    </div>

    <!-- INPUT -->
    <div class="p-4 border-t
          border-gray-200 dark:border-slate-500
          bg-white dark:bg-gray-900">

        <div class="flex gap-3">
            <input :placeholder="t('typeMessage')" class="flex-1 px-4 py-1.5 rounded-lg
                bg-gray-100 dark:bg-gray-800 dark:text-slate-200" v-model="message" @keyup.enter="sendMessage"
                @input="sendTyping" />

            <base-button icon="fa-solid fa-paper-plane text-blue-500 cursor-pointer" @click="sendMessage" />
        </div>

    </div>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { useMessageStore } from '@/stores/message.storage';
import { SendMessageType } from '@/types/common';
import ChatHeader from './component/Chat/ChatHeader.vue';
import { useConversation } from '@/composables/useConversation';
import FriendProfileUI from './component/FriendProfileUI.vue';
import { MessageEnum } from '@/types/enum';
import MessageContainer from './component/Chat/MessageContainer.vue';
import Modal from '@/components/Modal/Modal.vue';
import { useDateTime } from '@/composables/useDateTime';
import { socketSubscribe, sockJSSendMessage } from '@/utils/websocket';
import { StompSubscription } from '@stomp/stompjs';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';
import { useUserStore } from '@/stores/user.storage';
import { useScroll } from '@/composables/useScroll';
import { useSystemStore } from '@/stores/system.storage';
import { useDebounce } from '@/composables/useDebounce';

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const conversationStorage = useConversationStore()
const systemStorage = useSystemStore()
const userStorage = useUserStore()
const messageStorage = useMessageStore()
const { t } = useTranslate()
const { getRecipient } = useConversation()
const { getTime, formatSeparatorTime } = useDateTime()
const { onScroll, scrollToBottom } = useScroll()

const friendProfileModal = ref()
const message = ref('')

const typingUsers = ref<Map<number, any>>(new Map())

const scrollContainer = ref<HTMLElement | null>(null)

const emit = defineEmits(['update:isShowInfoSection'])

const sendMessage = async () => {
    if (!message.value.trim()) return

    const payload: SendMessageType = {
        content: message.value.trim(),
        conversationId: conversationStorage.conversation?.id,
        contentType: MessageEnum.TEXT
    }

    const success = await messageStorage.sendMessage(payload);

    if (success) {
        message.value = ''
        scrollToBottom(scrollContainer, true)
    }
}

const TIME_GAP = 5 * 60 * 1000 // 5 phút
const LONG_GAP = 60 * 60 * 1000 // 1 giờ

const messagesWithMeta = computed(() => {
    return messageStorage.messages.map((msg, index) => {
        const prev = messageStorage.messages[index - 1]
        const next = messageStorage.messages[index + 1]

        const currentTime = getTime(msg.ct)
        const prevTime = prev ? getTime(prev.ct) : 0
        const nextTime = next ? getTime(next.ct) : 0

        const isSamePrev = prev && prev.sender.id === msg.sender.id
        const isSameNext = next && next.sender.id === msg.sender.id

        // ✅ START OF GROUP (quan trọng nhất)
        const isStartGroup =
            !prev ||
            !isSamePrev ||
            (currentTime - prevTime > TIME_GAP)

        // 🔹 END GROUP (QUAN TRỌNG)
        const isEndGroup =
            !next ||
            !isSameNext ||
            (nextTime - currentTime > TIME_GAP)

        const isEndLongGap =
            next && (nextTime - currentTime > LONG_GAP)

        return {
            ...msg,

            // group theo sender (UI bo góc)
            isFirst: isStartGroup,
            isLast: !isSameNext,

            // ✅ avatar ở đầu group
            showAvatar: isStartGroup,

            // ✅ chỉ hiện time ở cuối group theo TIME_GAP
            showTime: isEndGroup || isEndLongGap,

            // separator
            showTimeSeparator:
                !prev ||
                (currentTime - prevTime > TIME_GAP) ||
                (currentTime - prevTime > LONG_GAP)
        }
    })
})

const { debounced: sendTyping } = useDebounce(() => {
    sockJSSendMessage({
        conversationId: conversationStorage.conversation?.id,
        username: userStorage.user?.username,
        userId: userStorage.user?.id,
    }, "chat.typing")
}, 500)

const scrollMore = () => {
    onScroll(scrollContainer, () => messageStorage.getMessages(conversationStorage.conversation!.id))
}

onMounted(async () => {
    systemStorage.setShowBottomMenu(false)
    subTyping = socketSubscribe(`/user/queue/chat.typing.${conversationStorage.conversation?.id}`, (msg: any) => {
        const data = JSON.parse(msg.body)

        // bỏ qua chính mình
        if (data.userId === userStorage.user?.id) return

        handleTyping(data)
        console.log(typingUsers.value.values())
    })

    messageStorage.resetPagination()

    await messageStorage.getMessages(conversationStorage.conversation!.id)

    scrollToBottom(scrollContainer, false)
})

let subTyping: StompSubscription | undefined

onUnmounted(() => {
    subTyping?.unsubscribe()
})

const handleTyping = (data: any) => {
    const userId = data.userId

    // nếu đã có thì clear timeout cũ
    if (typingUsers.value.has(userId)) {
        clearTimeout(typingUsers.value.get(userId).timeout)
    }

    // set timeout mới (2s)
    const timeout = setTimeout(() => {
        typingUsers.value.delete(userId)
    }, 2000)

    // update map
    typingUsers.value.set(userId, {
        ...data,
        timeout
    })
}
</script>

<style>
.dot {
    width: 4px;
    height: 4px;
    background-color: currentColor;
    border-radius: 50%;
    animation: bounce 1.4s infinite;
}

/* delay từng dot */
.dot:nth-child(1) {
    animation-delay: 0s;
}

.dot:nth-child(2) {
    animation-delay: 0.2s;
}

.dot:nth-child(3) {
    animation-delay: 0.4s;
}

@keyframes bounce {

    0%,
    80%,
    100% {
        transform: scale(0);
        opacity: 0.3;
    }

    40% {
        transform: scale(1);
        opacity: 1;
    }
}
</style>