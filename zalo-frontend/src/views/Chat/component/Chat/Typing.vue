<template>
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
import { useDebounce } from '@/composables/useDebounce';
import { useScroll } from '@/composables/useScroll';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { useMessageStore } from '@/stores/message.storage';
import { useUserStore } from '@/stores/user.storage';
import { SendMessageType } from '@/types/common';
import { MessageEnum } from '@/types/enum';
import { socketSubscribe, sockJSSendMessage } from '@/utils/websocket';
import { StompSubscription } from '@stomp/stompjs';
import { onMounted, onUnmounted, ref, watch } from 'vue';

const props = defineProps<{
    scrollContainer: HTMLElement | null
}>()

const { t } = useTranslate()
const conversationStorage = useConversationStore()
const userStorage = useUserStore()
const messageStorage = useMessageStore()
const { scrollToBottom } = useScroll()

const message = ref('')
const typingUsers = ref<Map<number, any>>(new Map())

const { debounced: sendTyping } = useDebounce(() => {
    sockJSSendMessage({
        conversationId: conversationStorage.conversation?.id,
        username: userStorage.user?.username,
        userId: userStorage.user?.id,
    }, "chat.typing")
}, 300)

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
        scrollToBottom(props.scrollContainer, true)
    }
}

let subTyping: StompSubscription | undefined

onMounted(() => {
    resetSubscribe()
})

onUnmounted(() => {
    subTyping?.unsubscribe()
})

watch(() => conversationStorage.conversation, async () => {
    subTyping?.unsubscribe()
    resetSubscribe()
})

const resetSubscribe = () => {
    subTyping = socketSubscribe(`/user/queue/chat.typing.${conversationStorage.conversation?.id}`, (msg: any) => {
        const data = JSON.parse(msg.body)

        // bỏ qua chính mình
        if (data.userId === userStorage.user?.id) return

        handleTyping(data)
        console.log(typingUsers.value.values())
    })
}

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