<template>
    <div class="w-full">
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

        <div class="w-[97%] overflow-x-auto">
            <div v-if="previewFiles.length > 0"
                class="flex flex-nowrap gap-3 p-3 w-1/2 border-t border-gray-200 dark:border-slate-700 bg-gray-50/50 dark:bg-gray-900/50 scrollbar-hide">

                <div v-for="(file, index) in previewFiles" :key="index" class="relative w-20 h-20 ...">
                    <img :src="file.url" class="w-full h-full object-cover"
                        :class="{ 'opacity-50': uploadProgress[index] < 100 }" />

                    <div v-if="uploadProgress[index] > 0 && uploadProgress[index] < 100"
                        class="absolute bottom-0 left-0 w-full h-3 bg-gray-200/50 z-20">
                        <div class="h-full bg-blue-500 transition-all duration-300"
                            :style="{ width: uploadProgress[index] + '%' }">
                        </div>
                        <span
                            class="absolute inset-0 flex items-center justify-center text-[10px] text-white font-black drop-shadow-md">
                            {{ uploadProgress[index] }}%
                        </span>
                    </div>

                    <div v-if="uploadProgress[index] === 100" class="absolute top-1 left-1 text-green-500">
                        ✅
                    </div>

                    <button @click="removeImage(index)"
                        class="absolute top-1 right-1 cursor-pointer bg-black/60 hover:bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-[10px] transition-colors shadow-md">
                        ✕
                    </button>
                </div>
            </div>
        </div>

        <!-- INPUT BAR -->
        <div class="p-1 md:p-3 border-t border-gray-200 dark:border-slate-700 dark:bg-gray-900">
            <div class="flex flex-col gap-2 bg-gray-100 dark:bg-gray-800 rounded-2xl px-2 py-0.5 md:px-4 md:py-1.5">
                <!-- Left icons -->
                <div class="flex gap-2 border-b pb-1" :class="[style.border.primary]">
                    <button @click.stop="toggleEmoji"
                        class="text-sm md:text-xl text-gray-500 dark:text-gray-400 hover:text-blue-500 transition cursor-pointer">
                        🙂
                    </button>

                    <input type="file" ref="fileInput" multiple accept="image/*,video/*" class="hidden"
                        @change="handleSelectImages" />

                    <button @click="fileInput?.click()"
                        class="text-sm md:text-xl text-gray-500 dark:text-gray-400 cursor-pointer">
                        📎
                    </button>
                </div>

                <!-- Message Input -->
                <div class="flex gap-2">
                    <input ref="inputRef" v-model="message" @keyup.enter="sendMessage" @input="sendTyping"
                        :placeholder="t('typeMessage')"
                        class="flex-1 bg-transparent outline-none text-xs md:text-base dark:text-slate-200 placeholder-gray-400 dark:placeholder-gray-500" />
                    <base-button icon="fa-solid fa-paper-plane text-blue-500 text-xs md:text-xl" @click="sendMessage"
                        class="ml-1" :disabled="isLoadingSendMessage" />
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
import { useUpload } from '@/composables/useUpload';
import { useConversationStore } from '@/stores/conversation.storage';
import { useMessageStore } from '@/stores/message.storage';
import { useUserStore } from '@/stores/user.storage';
import { SendMessageType, UploadFileRequest, UploadFileType } from '@/types/common';
import { MessageEnum, ModuleEnum, ResourceEnum } from '@/types/enum';
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
const { uploadFiles, imageFolder, videoFolder } = useUpload()
const { scrollToBottom } = useScroll()

const message = ref('')
const inputRef = ref(null)
const typingUsers = ref<Map<number, any>>(new Map())
const emojiWrapper = ref<any>(null)

const fileInput = ref<HTMLInputElement | null>(null)
const selectedRawFiles = ref<File[]>([])
const previewFiles = ref<{ url: string, type: ResourceEnum }[]>([])
const showEmoji = ref(false)
const isLoadingSendMessage = ref(false)
// State lưu trữ tiến độ upload để hiển thị UI
const uploadProgress = ref<Record<number, number>>({});

const handleSelectImages = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (!target.files) return;

    const files = Array.from(target.files);

    files.forEach(file => {
        // Tạo URL tạm thời trỏ đến file trong bộ nhớ trình duyệt
        const objectUrl = URL.createObjectURL(file);

        previewFiles.value.push({
            url: objectUrl,
            type: file.type.startsWith('video') ? ResourceEnum.VIDEO : ResourceEnum.IMAGE
        });

        selectedRawFiles.value.push(file);
    });

    // Reset input để có thể chọn lại cùng 1 file nếu cần
    target.value = '';
};

const removeImage = (index: number) => {
    previewFiles.value.splice(index, 1)
    selectedRawFiles.value.splice(index, 1)
}

const sendImages = async () => {
    if (selectedRawFiles.value.length === 0) return null;

    const params: UploadFileType[] = selectedRawFiles.value.map((file: File) => ({
        file,
        resourceType: file.type.startsWith('video') ? ResourceEnum.VIDEO : ResourceEnum.IMAGE,
        folder: file.type.startsWith('video') ? videoFolder : imageFolder
    }))

    const dto: UploadFileRequest = {
        params,
        updateProgress: (index: number, percent: number) => uploadProgress.value[index] = percent,
        moduleType: ModuleEnum.MESSAGE
    }

    const data = await uploadFiles(dto)
    console.log(data)

    // clear
    selectedRawFiles.value = []
    previewFiles.value = []

    return data
}

const toggleEmoji = () => {
    showEmoji.value = !showEmoji.value
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
    const content = message.value.trim() || null
    if (!content && previewFiles.value.length == 0) return

    isLoadingSendMessage.value = true

    const filesData = await sendImages()

    const payload: SendMessageType = {
        content,
        conversationId: conversationStorage.conversation?.id,
        contentType: filesData && filesData.length > 1 ? MessageEnum.IMAGE : MessageEnum.TEXT,
        attachments: filesData ? filesData : []
    }

    const success = await messageStorage.sendMessage(payload);

    if (success) {
        message.value = ''
        uploadProgress.value = {}
        scrollToBottom(props.scrollContainer, true)
    }

    isLoadingSendMessage.value = false
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