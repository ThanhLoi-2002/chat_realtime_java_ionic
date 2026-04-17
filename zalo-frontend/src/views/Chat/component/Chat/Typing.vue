<template>
    <div class="w-full relative">
        <!-- Typing indicator -->
        <TypingIndicator :users="typingUsers" />

        <div ref="emojiWrapper">
            <EmojiPicker v-show="showEmoji" class="fixed bottom-30" @select="handleSelectEmoji" />
        </div>

        <FilePreview :files="previewFiles" @remove="removeImage" />

        <div class="relative w-full">
            <MentionSuggestions :suggestions="mentionSuggestions" :selected-index="selectedIndex"
                @select="insertMention" />

            <LinkPreview v-if="previewLink" :metadata="previewLink" @close="previewLink = undefined" />

            <!-- INPUT BAR -->
            <div class="w-full p-1 md:p-3 border-t border-gray-200 dark:border-slate-700 dark:bg-gray-900">
                <div class="flex flex-col gap-2 bg-gray-100 dark:bg-gray-800 rounded-2xl px-2 py-0.5 md:px-4 md:py-1.5">
                    <!-- Left icons -->
                    <InputActions @toggle-emoji="toggleEmoji" @select-media="handleSelectImages"
                        @select-doc="handleDirectUploadAndSend" />

                    <!-- Message Input -->
                    <div class="flex flex-col w-full gap-2 items-center">
                        <ReplyingMessage v-if="replyingMessage" :replyingMessage="replyingMessage" :setReplyingMessage="setReplyingMessage"/>

                        <div class="flex gap-2 w-full">
                            <div class="relative flex-1 bg-gray-100 dark:bg-gray-800 rounded-2xl px-1 py-2">
                                <div ref="inputRef" contenteditable="true"
                                    class="outline-none text-xs md:text-base dark:text-slate-200 min-h-[1.5em] max-h-[3em] overflow-y-auto wrap-break-word"
                                    :placeholder="t('typeMessage')" @input="onInput" @keydown="onKeyDown" />
                            </div>

                            <div v-if="isLoadingSendMessage">
                                <LoadingSpinner />
                            </div>

                            <base-button v-else icon="fa-solid fa-paper-plane text-blue-500 text-xs md:text-xl"
                                @click="sendMessage" class="ml-1" />
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>
<script setup lang="ts">
import EmojiPicker from '@/components/Emoji/EmojiPicker.vue';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';
import { useDebounce } from '@/composables/useDebounce';
import { useMessage } from '@/composables/useMessage';
import { useScroll } from '@/composables/useScroll';
import { useTranslate } from '@/composables/useTranslate';
import { useUpload } from '@/composables/useUpload';
import { useConversationStore } from '@/stores/conversation.storage';
import { useMessageStore } from '@/stores/message.storage';
import { useUserStore } from '@/stores/user.storage';
import { LinkMetadataType, SendMessageType, UploadFileRequest, UploadFileType } from '@/types/common';
import { MessageEnum, ModuleEnum, ResourceEnum } from '@/types/enum';
import { socketSubscribe, sockJSSendMessage } from '@/utils/websocket';
import { StompSubscription } from '@stomp/stompjs';
import { nextTick, onMounted, onUnmounted, ref, watch } from 'vue';
import LinkPreview from './LinkPreview.vue';
import MentionSuggestions from './MentionSuggestions.vue';
import InputActions from './InputActions.vue';
import FilePreview from './FilePreview.vue';
import TypingIndicator from './TypingIndicator.vue';
import { MessageType, UserType } from '@/types/entities';
import ReplyingMessage from '../Message/ReplyingMessage.vue';

const props = defineProps<{
    scrollContainer: HTMLElement | null
    replyingMessage: MessageType | null
    setReplyingMessage: (m: MessageType | null) => void
}>()

const { t } = useTranslate()
const conversationStorage = useConversationStore()
const userStorage = useUserStore()
const messageStorage = useMessageStore()
const { uploadFiles, imageFolder, videoFolder, rawFolder } = useUpload()
const { scrollToBottom } = useScroll()

const message = ref('')
const inputRef = ref(null)
const typingUsers = ref<Map<number, any>>(new Map())
const emojiWrapper = ref<any>(null)

const selectedRawFiles = ref<File[]>([])
const previewFiles = ref<{ url: string, type: ResourceEnum, name?: string }[]>([])
const showEmoji = ref(false)
const isLoadingSendMessage = ref(false)
const previewLink = ref<LinkMetadataType | undefined>(undefined)
const { onPreviewLink } = useMessage()

const { debounced: showPreviewLink } = useDebounce(async () => {
    previewLink.value = await onPreviewLink(message.value)
}, 500)

watch(() => (inputRef.value as any)?.innerText, () => {
    showPreviewLink()
})

// State lưu trữ tiến độ upload để hiển thị UI
const uploadProgress = ref<Record<number, number>>({});

const mentionSuggestions = ref<any[]>([]);

const handleDirectUploadAndSend = async (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (!target.files || target.files.length === 0) return;

    const files = Array.from(target.files);
    isLoadingSendMessage.value = true;

    try {
        // 1. Chuẩn bị dữ liệu upload
        const params: UploadFileType[] = files.map((file) => ({
            file,
            resourceType: ResourceEnum.RAW, // Hoặc loại tương ứng backend quy định cho tài liệu
            folder: rawFolder
        }));

        const dto: UploadFileRequest = {
            params,
            updateProgress: (index: number, percent: number) => {
                // Bạn có thể dùng một biến progress tổng hoặc bỏ qua nếu không hiện preview
                console.log(`Uploading file ${index}: ${percent}%`);
            },
            moduleType: ModuleEnum.MESSAGE
        };

        // 2. Thực hiện Upload lên server
        const filesData = await uploadFiles(dto);

        if (filesData && filesData.length > 0) {
            // 3. Lặp qua từng file đã upload thành công để gửi từng message
            const sendPromises = filesData.map((singleFile) => {
                const payload: SendMessageType = {
                    content: "", // Để trống hoặc dùng singleFile.name nếu backend hỗ trợ
                    conversationId: conversationStorage.conversation?.id,
                    contentType: MessageEnum.FILE,
                    // Mỗi message chỉ chứa 1 file duy nhất trong mảng attachments
                    attachments: [singleFile]
                };

                return messageStorage.sendMessage(payload);
            });

            // Đợi tất cả tin nhắn được gửi đi
            const results = await Promise.all(sendPromises);

            // Nếu có ít nhất một tin nhắn thành công thì cuộn xuống
            if (results.some(success => success)) {
                scrollToBottom(props.scrollContainer, true);
            }
        }
    } catch (error) {
        console.error("Gửi file thất bại:", error);
    } finally {
        isLoadingSendMessage.value = false;
        target.value = ''; // Reset input để có thể chọn lại file đó lần sau
    }
};

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

    const params: UploadFileType[] = selectedRawFiles.value.map((file: File) => {
        const isVideo = file.type.startsWith('video');
        const isImage = file.type.startsWith('image');
        const folder = isVideo ? videoFolder : (isImage ? imageFolder : rawFolder)
        console.log(folder, file.type)

        return {
            file,
            // Nếu không phải ảnh/video thì mặc định là RAW
            resourceType: isVideo ? ResourceEnum.VIDEO : (isImage ? ResourceEnum.IMAGE : ResourceEnum.RAW),
            folder
        };
    });

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

const handleSelectEmoji = (emoji: string) => {
    const inputElement: any = inputRef.value;
    if (!inputElement) return;

    // Đảm bảo input đang focus
    inputElement.focus();

    const selection = window.getSelection();
    if (!selection || !selection.rangeCount) return;

    const range = selection.getRangeAt(0);

    // 1. Xóa nội dung đang chọn (nếu có)
    range.deleteContents();

    // 2. Tạo một Text Node chứa emoji
    const emojiNode = document.createTextNode(emoji);

    // 3. Chèn emoji vào vị trí con trỏ
    range.insertNode(emojiNode);

    // 4. Di chuyển con trỏ ra sau emoji vừa chèn
    range.setStartAfter(emojiNode);
    range.setEndAfter(emojiNode);
    selection.removeAllRanges();
    selection.addRange(range);

    // 5. Cập nhật lại giá trị biến message
    message.value = inputElement.innerText;

    // Đóng picker
    showEmoji.value = false;
};

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

    // 3. QUAN TRỌNG: Dùng textContent để lấy chuỗi đã biến đổi
    // Lúc này outerHTML đã biến thành text thường trong tempDiv
    const finalContent = tempDiv.textContent?.trim() || "";

    const payload: SendMessageType = {
        content: finalContent,
        conversationId: conversationStorage.conversation?.id,
        contentType: filesData && filesData.length > 1 ? MessageEnum.IMAGE : MessageEnum.TEXT,
        attachments: filesData ? filesData : [],
        linkMetadata: previewLink.value,
        replyToId: props.replyingMessage?.id
    }

    const success = await messageStorage.sendMessage(payload);

    if (success) {
        // QUAN TRỌNG: Xóa trắng thẻ div contenteditable
        if (inputElement) {
            inputElement.innerHTML = '';
        }
        message.value = ''; // Reset biến message
        uploadProgress.value = {};
        previewLink.value = undefined
        props.setReplyingMessage(null)
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

watch(() => conversationStorage.conversation?.id, async () => {
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
const insertMention = (user: UserType) => {
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

        tagNode.setAttribute('data-user-id', user.id.toString());

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

const insertReplyMention = (user: UserType) => {
    const inputElement: any = inputRef.value;
    if (!inputElement) return;

    // 1. Focus vào input
    inputElement.focus();

    // 2. Tạo thẻ tag (giống định dạng bạn đang dùng cho @mention)
    const tagNode = document.createElement('span');
    tagNode.className = 'mention-tag';
    tagNode.contentEditable = 'false';
    tagNode.innerText = `@${user.username}`;
    tagNode.style.color = '#3b82f6';
    tagNode.style.fontWeight = 'bold';
    tagNode.setAttribute('data-user-id', user.id.toString());

    // 3. Chèn vào đầu input hoặc vị trí con trỏ
    const selection = window.getSelection();
    if (selection && selection.rangeCount > 0) {
        const range = selection.getRangeAt(0);
        
        // Nếu input đang trống, chèn vào đầu. Nếu có chữ, chèn tại vị trí caret.
        range.insertNode(tagNode);
        
        // Tạo dấu cách sau tag
        const space = document.createTextNode('\u00A0');
        tagNode.after(space);

        // Đưa con trỏ ra sau dấu cách để người dùng gõ tiếp
        range.setStartAfter(space);
        range.setEndAfter(space);
        selection.removeAllRanges();
        selection.addRange(range);
    }
};

watch(() => props.replyingMessage, () => {
    if(props.replyingMessage && props.replyingMessage.sender.id != userStorage.user?.id) insertReplyMention(props.replyingMessage.sender)
})

const onInput = (e: Event) => {
    const target = e.target as HTMLElement;
    // Cập nhật giá trị text để xử lý gợi ý tag
    message.value = target.innerText;
    checkMention();
    sendTyping()
    showPreviewLink()
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
[contenteditable=true]:empty:before {
    content: attr(placeholder);
    pointer-events: none;
    display: block;
    /* For Firefox */
    color: #9ca3af;
}
</style>