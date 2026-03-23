<template>
    <!-- HEADER -->
    <ChatHeader :isShowInfoSection="isShowInfoSection"
        @update:isShowInfoSection="val => emit('update:isShowInfoSection', val)" />

    <!-- MESSAGES -->
    <div class="flex-1 overflow-y-auto p-6 space-y-4">
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

    <!-- INPUT -->
    <footer class="p-4 border-t
          border-gray-200 dark:border-slate-500
          bg-white dark:bg-gray-900">

        <div class="flex gap-3">
            <input :placeholder="t('typeMessage')" class="flex-1 px-4 py-1.5 rounded-lg
                bg-gray-100 dark:bg-gray-800 dark:text-slate-200" v-model="message" @keyup.enter="sendMessage" @input="typing"/>

            <base-button icon="fa-solid fa-paper-plane text-blue-500 cursor-pointer" @click="sendMessage" />
        </div>

    </footer>
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

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const conversationStorage = useConversationStore()
const messageStorage = useMessageStore()
const { t } = useTranslate()
const { getRecipient } = useConversation()
const { getTime, formatSeparatorTime } = useDateTime()

const friendProfileModal = ref()
const message = ref('')

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
            isFirst: !isSamePrev,
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

onMounted(() => {
    messageStorage.getMessages(conversationStorage.conversation!.id)
})

let subTyping: StompSubscription | undefined

onMounted(() => {
  subTyping = socketSubscribe(`/topic/chat.typing.${conversationStorage.conversation?.id}`, (msg: any) => {
    console.log("typing:", JSON.parse(msg.body))
  })

})

onUnmounted(() => {
  subTyping?.unsubscribe()
})

const typing = () => {
  sockJSSendMessage({
    conversationId: conversationStorage.conversation?.id,
    content: 'Lợi đang nhập'
  }, "chat.typing")
}
</script>