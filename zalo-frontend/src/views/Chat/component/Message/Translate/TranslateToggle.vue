<template>
    <div>
        <div v-if="isTranslated && translatedRawText"
            class="flex flex-col bg-slate-300/50 dark:bg-slate-400/50 px-1.5 py-1 rounded-md animate-fade-in">
            <span class="text-[9px] font-bold tracking-wider text-blue-500 dark:text-blue-300 mb-0.5">
                {{ t("translated") }}
            </span>
            <span class="" :class="isOwner ? 'text-slate-50 dark:text-white/90' : 'text-slate-700 dark:text-slate-200'"
                v-html="formattedTranslatedContent">
            </span>
        </div>

        <div v-if="shouldShowTranslateBtn && message.stt !== -1" class="mt-1 flex items-center gap-1">
            <button @click="toggleTranslate" :disabled="isTranslating"
                class="text-[11px] font-medium text-blue-700/90 dark:text-blue-400 cursor-pointer flex items-center gap-1 bg-transparent border-none p-0 focus:outline-none">
                <i v-if="isTranslating" class="fa-solid fa-spinner fa-spin text-[10px]"></i>
                <i v-else class="fa-solid fa-language"></i>
                {{ isTranslated ? t("hideTranslation") : t("translateMessage") }}
            </button>
        </div>
    </div>
</template>
<script setup lang="ts">
import { useMessage } from '@/composables/useMessage';
import { useTranslate } from '@/composables/useTranslate';
import { useLangStore } from '@/stores/lang.storage';
import { useTranslateStore } from '@/stores/translate.storage';
import { MessageType } from '@/types/entities';
import { computed, ref } from 'vue';

const props = defineProps<{
    message: MessageType
    isOwner: boolean
}>()

const langStorage = useLangStore()
const { t } = useTranslate();
const { formattedContentWithTag } = useMessage()
const translateStorage = useTranslateStore()

const isTranslated = ref(false);       // Trạng thái: Đang hiển thị bản dịch hay không
const isTranslating = ref(false);      // Trạng thái: Đang load API dịch
const translatedRawText = ref("");     // Lưu text thô nhận về từ API dịch

const currentAppLang = computed(() => {
    if (langStorage.lang == 'cn' || langStorage.lang == 'tw')
        return 'zh'
    else
        return langStorage.lang;
});

// Điều kiện hiển thị nút dịch: Khác ngôn ngữ và tin nhắn phải có nội dung
const shouldShowTranslateBtn = computed(() => {
    if (!props.message?.content || !props.message?.lang) return false;
    return props.message.lang.toLowerCase() !== currentAppLang.value.toLowerCase();
});

// Xử lý Format Regex URL + Mention cho nội dung sau khi dịch giống y hệt nội dung gốc
const formattedTranslatedContent = computed(() => {
    if (!translatedRawText.value) return "";

    const content = formattedContentWithTag(translatedRawText.value, props.isOwner);
    const urlRegex = /(https?:\/\/[^\s]+|www\.[^\s]+)/g;

    return content.replace(urlRegex, (url) => {
        const href = url.startsWith('http') ? url : `https://${url}`;
        return `<span class="text-blue-700 dark:text-blue-400 chat-url cursor-pointer underline break-all" data-url="${href}">${url}</span>`;
    });
});

// Hàm xử lý khi bấm nút Dịch / Ẩn bản dịch
const toggleTranslate = async () => {
    // Nếu đang hiển thị bản dịch thì bấm vào sẽ ẩn nó đi (Bản gốc vẫn giữ nguyên)
    if (isTranslated.value) {
        isTranslated.value = false;
        return;
    }

    // Nếu chưa có dữ liệu dịch trong bộ nhớ cache local thì tiến hành call API
    if (!translatedRawText.value) {
        isTranslating.value = true;
        translatedRawText.value = await translateStorage.translateMessage(props.message, currentAppLang.value)
        isTranslated.value = !!translatedRawText.value;

        isTranslating.value = false;
    } else {
        // Nếu đã dịch từ trước đó rồi (đã có cache), chỉ cần bật lại giao diện block dịch mà không cần gọi lại API
        isTranslated.value = true;
    }
};
</script>