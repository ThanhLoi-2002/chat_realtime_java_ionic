<template>
    <div>
        <!-- Typing indicator (giữ nguyên) -->
        <div v-if="typingUsers.size > 0"
            class="text-sm text-gray-600 dark:text-gray-400 px-4 pb-2 flex items-center gap-1">
            <span>
                <span v-for="([id, user], index) in typingUsers" :key="id">
                    <span v-if="index > 0">, </span>{{ user.username }}
                </span>
                {{ t("typing") }}
            </span>
            <span class="flex gap-1 ml-1">
                <span class="dot"></span>
                <span class="dot"></span>
                <span class="dot"></span>
            </span>
        </div>

        <div ref="emojiWrapper">
            <EmojiPicker v-show="showEmoji" class="fixed bottom-30" @select="handleSelectEmoji" />
        </div>

        <div v-if="previewImages.length > 0" class="flex gap-2 p-2 overflow-x-auto">

            <div v-for="(img, index) in previewImages" :key="index"
                class="relative w-20 h-20 rounded-xl overflow-hidden">
                <img :src="img" class="w-full h-full object-cover" />

                <!-- nút xoá -->
                <button @click="removeImage(index)"
                    class="absolute top-1 right-1 bg-black/50 text-white rounded-full px-1 text-xs">
                    ✕
                </button>
            </div>
        </div>

        <!-- INPUT BAR -->
        <div class="p-3 border-t border-gray-200 dark:border-slate-700 bg-white dark:bg-gray-900">
            <div class="flex flex-col gap-2 bg-gray-100 dark:bg-gray-800 rounded-2xl px-4 py-1.5">
                <!-- Left icons -->
                <div class="flex gap-2 border-b pb-1" :class="[style.border.primary]">
                    <button @click.stop="toggleEmoji"
                        class="text-2xl text-gray-500 dark:text-gray-400 hover:text-blue-500 transition cursor-pointer">
                        🙂
                    </button>

                    <input type="file" ref="fileInput" multiple accept="image/*" class="hidden"
                        @change="handleSelectImages" />

                    <button @click="fileInput?.click()"
                        class="text-2xl text-gray-500 dark:text-gray-400 cursor-pointer">
                        📎
                    </button>
                </div>

                <!-- Message Input -->
                <div class="flex gap-2">
                    <input ref="inputRef" v-model="message" @keyup.enter="sendMessage" @input="sendTyping"
                        :placeholder="t('typeMessage')"
                        class="flex-1 bg-transparent outline-none text-base dark:text-slate-200 placeholder-gray-400 dark:placeholder-gray-500" />
                    <base-button icon="fa-solid fa-paper-plane text-blue-500 text-xl" @click="sendMessage"
                        class="ml-1" />
                </div>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import EmojiPicker from '@/components/Emoji/EmojiPicker.vue';
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
import { nextTick, onMounted, onUnmounted, ref, watch } from 'vue';

const props = defineProps<{
    scrollContainer: HTMLElement | null
}>()

const { t } = useTranslate()
const conversationStorage = useConversationStore()
const userStorage = useUserStore()
const messageStorage = useMessageStore()
const { scrollToBottom } = useScroll()

const message = ref('')
const inputRef = ref(null)
const typingUsers = ref<Map<number, any>>(new Map())
const emojiWrapper = ref<any>(null)

const fileInput = ref<HTMLInputElement | null>(null)
const selectedImages = ref<File[]>([])
const previewImages = ref<string[]>([])
const showEmoji = ref(false)

const handleSelectImages = (e: Event) => {
    const input = e.target as HTMLInputElement
    const files = Array.from(input.files || [])

    selectedImages.value = [...selectedImages.value, ...files]

    // tạo preview
    files.forEach((file: File) => {
        const url = URL.createObjectURL(file)
        previewImages.value.push(url)
    })

    // reset input để chọn lại cùng file vẫn trigger
    // e.target.value = ''
}

const removeImage = (index: number) => {
    previewImages.value.splice(index, 1)
    selectedImages.value.splice(index, 1)
}

const sendImages = async () => {
    for (const file of selectedImages.value) {
        await messageStorage.sendMessage({
            file,
            conversationId: conversationStorage.conversation?.id,
            contentType: MessageEnum.IMAGE
        })
    }

    // clear
    selectedImages.value = []
    previewImages.value = []
}

const toggleEmoji = () => {
    showEmoji.value = !showEmoji.value
    console.log(showEmoji.value)
}

const { debounced: sendTyping } = useDebounce(() => {
    sockJSSendMessage({
        conversationId: conversationStorage.conversation?.id,
        username: userStorage.user?.username,
        userId: userStorage.user?.id,
    }, "chat.typing")
}, 300)

const handleSelectEmoji = (emoji: any) => {
    const input: any = inputRef.value
    if (!input) return

    const start = input.selectionStart
    const end = input.selectionEnd

    const text = message.value

    // 👉 chèn emoji vào đúng vị trí cursor
    message.value =
        text.slice(0, start) +
        emoji +
        text.slice(end)

    // 👉 giữ focus + set lại cursor
    nextTick(() => {
        input.focus()
        input.selectionStart = input.selectionEnd = start + emoji.length
    })
}

const sendMessage = async () => {
    if (selectedImages.value.length) {
        await sendImages()
    }

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

const handleClickOutside = (event: any) => {
    if (!emojiWrapper.value) return

    // nếu click KHÔNG nằm trong picker → đóng
    if (!emojiWrapper.value.contains(event.target)) {
        showEmoji.value = false
    }
}

let subTyping: StompSubscription | undefined

onMounted(() => {
    document.addEventListener('click', handleClickOutside)
    resetSubscribe()
})

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside)
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