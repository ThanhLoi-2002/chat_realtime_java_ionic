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

        <div class="relative">
            <div v-if="mentionSuggestions.length > 0" class="absolute bg-white dark:bg-gray-800 border border-gray-200 dark:border-slate-700 rounded-xl shadow-2xl z-9999 max-h-48 overflow-y-auto 
                w-fit min-w-50 bottom-full left-8">
                <div v-for="(user, index) in mentionSuggestions" :key="user.id" @mousedown.prevent="insertMention(user)"
                    class="flex items-center gap-2 px-4 py-3 cursor-pointer border-b border-gray-100 dark:border-slate-700 last:border-0"
                    :class="{ 'bg-blue-100 dark:bg-slate-700': index === selectedIndex }">
                    <CircleAvatar :user="user" :is-disabled="true" size="w-8 h-8" />
                    <span class="text-xs dark:text-slate-200"
                        :class="{ 'font-bold text-blue-600': index === selectedIndex }">
                        @{{ user.username }}
                    </span>
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
                        <!-- <input ref="inputRef" v-model="message" @keyup.enter="sendMessage" @input="sendTyping"
                            :placeholder="t('typeMessage')"
                            class="flex-1 bg-transparent outline-none text-xs md:text-base dark:text-slate-200 placeholder-gray-400 dark:placeholder-gray-500" /> -->
                        <div class="relative flex-1 bg-gray-100 dark:bg-gray-800 rounded-2xl px-4 py-2">
                            <div ref="inputRef" contenteditable="true"
                                class="outline-none text-xs md:text-base dark:text-slate-200 min-h-[1.5em] max-h-32 overflow-y-auto wrap-break-word"
                                :placeholder="t('typeMessage')" @input="onInput" @keydown="onKeyDown">
                            </div>
                        </div>
                        <base-button icon="fa-solid fa-paper-plane text-blue-500 text-xs md:text-xl"
                            @click="sendMessage" class="ml-1" :disabled="isLoadingSendMessage" />
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
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

const mentionSuggestions = ref<any[]>([]);

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

    const inputElement: any = inputRef.value;
    // Lấy nội dung từ div thay vì chỉ dùng message.value
    const content = inputElement?.innerText.trim() || null;

    // Kiểm tra nếu không có chữ và cũng không có file thì thoát
    if (!content && previewFiles.value.length === 0) return;

    isLoadingSendMessage.value = true

    // Upload file
    const filesData = await sendImages()

    // Copy nội dung để xử lý mà không ảnh hưởng UI
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = inputElement.innerHTML;

    // Tìm tất cả các thẻ span.mention-tag mà bạn đã chèn vào trước đó
    const tags = tempDiv.querySelectorAll('.mention-tag');
    
    tags.forEach(tag => {
        const userId = tag.getAttribute('data-user-id');
        const username = tag.textContent;
        // Thay thế node bằng chuỗi văn bản có cấu trúc
        tag.outerHTML = `[mention:${userId}]${username}[/mention]`;
    });

    console.log(tempDiv.textContent)

    // 3. QUAN TRỌNG: Dùng textContent để lấy chuỗi đã biến đổi
    // Lúc này outerHTML đã biến thành text thường trong tempDiv
    const finalContent = tempDiv.textContent?.trim() || "";

    const payload: SendMessageType = {
        content: finalContent,
        conversationId: conversationStorage.conversation?.id,
        contentType: filesData && filesData.length > 1 ? MessageEnum.IMAGE : MessageEnum.TEXT,
        attachments: filesData ? filesData : []
    }

    const success = await messageStorage.sendMessage(payload);

    if (success) {
        // QUAN TRỌNG: Xóa trắng thẻ div contenteditable
        if (inputElement) {
            inputElement.innerHTML = '';
        }
        message.value = ''; // Reset biến message
        uploadProgress.value = {};
        scrollToBottom(props.scrollContainer, true);
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

const selectedIndex = ref(0); // Chỉ số người dùng đang chọn trong list

// Reset index về 0 mỗi khi danh sách gợi ý thay đổi
watch(mentionSuggestions, (newVal) => {
    if (newVal.length > 0) {
        selectedIndex.value = 0;
    }
});

watch(selectedIndex, (newIdx) => {
    nextTick(() => {
        const activeItem = document.querySelector('.bg-blue-100.dark\\:bg-slate-700');
        if (activeItem) {
            activeItem.scrollIntoView({ block: 'nearest' });
        }
    });
});

const onKeyDown = (e: KeyboardEvent) => {
    // TRƯỜNG HỢP 1: Đang hiện danh sách gợi ý
    if (mentionSuggestions.value.length > 0) {
        if (e.key === 'ArrowDown') {
            e.preventDefault();
            selectedIndex.value = (selectedIndex.value + 1) % mentionSuggestions.value.length;
        }
        else if (e.key === 'ArrowUp') {
            e.preventDefault();
            selectedIndex.value = (selectedIndex.value - 1 + mentionSuggestions.value.length) % mentionSuggestions.value.length;
        }
        else if (e.key === 'Enter' || e.key === 'Tab') {
            e.preventDefault(); // Chặn không cho xuống dòng và không cho gửi tin nhắn
            if (mentionSuggestions.value[selectedIndex.value]) {
                insertMention(mentionSuggestions.value[selectedIndex.value]);
            }
        }
        else if (e.key === 'Escape') {
            e.preventDefault();
            mentionSuggestions.value = [];
        }
        return; // Thoát hàm tại đây, không chạy xuống logic bên dưới
    }

    // TRƯỜNG HỢP 2: Không có gợi ý, xử lý Enter để gửi tin nhắn
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
};

// 2. Hàm xử lý khi gõ phím để tìm @
const checkMention = () => {
    const selection = window.getSelection();
    if (!selection || !selection.rangeCount) return;

    const range = selection.getRangeAt(0);
    const textBeforeCaret = range.startContainer.textContent?.slice(0, range.startOffset) || '';

    // Tìm dấu @ cuối cùng trước con trỏ
    const lastAtSymbol = textBeforeCaret.lastIndexOf('@');

    if (lastAtSymbol !== -1) {
        const query = textBeforeCaret.slice(lastAtSymbol + 1).toLowerCase();

        // Điều kiện: Không có khoảng trắng sau dấu @
        if (!query.includes(' ')) {
            const members = conversationStorage.conversation?.members || [];

            mentionSuggestions.value = members.filter((m: any) => {
                const isMatch = m.username.toLowerCase().includes(query);
                const isNotMe = m.id !== userStorage.user?.id;

                // Kiểm tra xem đã tag chưa (dựa trên text của toàn bộ div)
                const fullText = (inputRef.value as any)?.innerText || '';
                const alreadyTagged = fullText.includes(`@${m.username}`);

                return isMatch && isNotMe && !alreadyTagged;
            });
            return;
        }
    }
    mentionSuggestions.value = [];
};

// 3. Hàm chèn tên khi click vào danh sách
const insertMention = (user: any) => {
    const selection = window.getSelection();
    if (!selection || !selection.rangeCount) return;

    const range = selection.getRangeAt(0);
    const textNode = range.startContainer;
    const offset = range.startOffset;
    const textContent = textNode.textContent || '';

    // Tìm vị trí dấu @ để xóa
    const lastAtSymbol = textContent.lastIndexOf('@', offset - 1);

    if (lastAtSymbol !== -1) {
        // 1. Xóa đoạn "@query" cũ
        range.setStart(textNode, lastAtSymbol);
        range.setEnd(textNode, offset);
        range.deleteContents();

        // 2. Tạo thẻ Tag màu xanh
        const tagNode = document.createElement('span');
        tagNode.className = 'mention-tag'; // Class CSS đã viết ở câu trước
        tagNode.contentEditable = 'false'; // Quan trọng: để Backspace xóa cả cụm
        tagNode.innerText = `@${user.username}`;
        tagNode.style.color = '#3b82f6'; // Thêm màu trực tiếp nếu ko muốn dùng CSS file
        tagNode.style.fontWeight = 'bold';

        tagNode.setAttribute('data-user-id', user.id);

        // 3. Chèn vào văn bản
        range.insertNode(tagNode);

        // 4. Chèn thêm 1 dấu cách sau tag để người dùng gõ tiếp
        const space = document.createTextNode('\u00A0'); // Dấu cách đặc biệt
        tagNode.after(space);

        // 5. Đặt con trỏ ra sau dấu cách
        const newRange = document.createRange();
        newRange.setStartAfter(space);
        newRange.collapse(true);
        selection.removeAllRanges();
        selection.addRange(newRange);
    }

    mentionSuggestions.value = []; // Phải clear list
    selectedIndex.value = 0;       // Reset index
};

const onInput = (e: Event) => {
    const target = e.target as HTMLElement;
    // Cập nhật giá trị text để xử lý gợi ý tag
    message.value = target.innerText;
    checkMention();
    sendTyping()
};

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

[contenteditable=true]:empty:before {
    content: attr(placeholder);
    pointer-events: none;
    display: block;
    /* For Firefox */
    color: #9ca3af;
}

/* Style cho thẻ tag bên trong input */
.mention-tag {
    color: #3b82f6;
    /* Blue-500 */
    font-weight: bold;
    background-color: rgba(59, 130, 246, 0.1);
    padding: 0 4px;
    border-radius: 4px;
    user-select: all;
    /* Chọn cả cụm khi click */
}
</style>