<template>
    <div class="">
        <div v-if="showMenu" ref="menuRef" :style="menuInlineStyle" class="fixed z-50 w-56
           bg-white dark:bg-gray-800 rounded-lg shadow-lg border
           border-gray-200 dark:border-gray-700 overflow-hidden
           transition-opacity duration-150 text-sm">
            <div :class="['p-2 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer', style.text.primary]"
                @click="onCopy">
                {{ t('copyMessage') }}
            </div>
            <div :class="['p-2 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer', style.text.primary]"
                @click="onMark">
                {{ t('markMessage') }}
            </div>
            <div :class="['p-2 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer', style.text.primary]"
                @click="onPin">
                {{ t('pinMessage') }}
            </div>
            <div :class="['p-2 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer', style.text.primary]"
                @click="onDetails">
                {{ t('seeDetails') }}
            </div>
            <div :class="['p-2 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer', style.text.primary]"
                @click="onSelectMessages">
                {{ t('selectMultipleMessages') }}
            </div>

            <div class="border-t border-gray-200 dark:border-gray-700"></div>

            <div v-if="userStorage.user?.id == message.sender?.id"
                class="p-2 text-red-500 hover:bg-red-50 dark:hover:bg-red-700/20 cursor-pointer"
                @click="onOpenDeleteConfirm">
                {{ t('delete') }}
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import { useTranslate } from '@/composables/useTranslate';
import { usePinStore } from '@/stores/pin.storage';
import { useUserStore } from '@/stores/user.storage';
import { MessageType } from '@/types/entities';
import { toast } from '@/utils/toast';
import { ref } from 'vue';
import { useConfirmStore } from '@/composables/useConfirm';
import { useMessageStore } from '@/stores/message.storage';
import { useChatActionStore } from '@/composables/useChatAction';

const props = defineProps<{
    message: MessageType
    showMenu: boolean
    menuInlineStyle: any
    onDetails: () => void
}>()
const { t } = useTranslate()
const userStorage = useUserStore()
const pinStorage = usePinStore()
const confirmStore = useConfirmStore();
const messageStorage = useMessageStore()
const actionStore = useChatActionStore();

const emit = defineEmits(['update:showMenu'])

// Trong MessageContextMenu.vue
const menuRef = ref<HTMLElement | null>(null)

// BẮT BUỘC phải thêm dòng này để cha nhận được ref
defineExpose({
    menuRef
})

// Actions (placeholders — replace with your actual logic)
const onCopy = async () => {
    const textToCopy = props.message.content || '';

    if (!textToCopy) return;

    // Cách 1: Dùng Clipboard API (Hiện đại)
    if (navigator.clipboard && window.isSecureContext) {
        try {
            await navigator.clipboard.writeText(textToCopy);
            toast({ message: t("copied"), color: "success" }); // Thông báo thành công
        } catch (err) {
            fallbackCopy(textToCopy);
        }
    } else {
        // Cách 2: Fallback cho HTTP hoặc trình duyệt cũ
        fallbackCopy(textToCopy);
    }

    emit('update:showMenu', false);
};

// Hàm bổ trợ copy thủ công
const fallbackCopy = (text: string) => {
    const textArea = document.createElement("textarea");
    textArea.value = text;

    // Tránh bị scroll trang khi append
    textArea.style.position = "fixed";
    textArea.style.left = "-9999px";
    textArea.style.top = "0";

    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try {
        const successful = document.execCommand('copy');
        if (successful) {
            toast({ message: t("copied"), color: "success" });
        }
    } catch (err) {
        console.error('Fallback copy failed', err);
    }

    document.body.removeChild(textArea);
};

const onMark = () => {
    // implement marking logic
    emit('update:showMenu', false);
}

const onPin = async () => {
    await pinStorage.pinMess(props.message.id, props.message.conversationId)
    emit('update:showMenu', false);
}

const onOpenDeleteConfirm = () => {
    emit('update:showMenu', false)
    confirmStore.open({
        title: t('deleteMessage'),
        message: t('deleteMessage'), // Bạn có thể đổi text linh hoạt
        onOk: () => {
            messageStorage.deleteMessage(props.message.id, props.message.conversationId)
        }
    });
}

const onSelectMessages = () => {
    actionStore.isSelectionMode = true;
    actionStore.shareMessages.push(props.message);
    emit('update:showMenu', false);
}
</script>