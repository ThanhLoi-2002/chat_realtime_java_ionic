import { defineStore } from 'pinia';
import { ref } from 'vue';
import { MessageType } from '@/types/entities';

export const useChatActionStore = defineStore('chatAction', () => {
    // Context Menu State
    const activeMessage = ref<MessageType | null>(null);
    const showMenu = ref(false);
    const menuInlineStyle = ref<Record<string, string>>({ opacity: '0' });
    const menuRef = ref<HTMLElement | null>(null);

    // Share Modal State
    const showShareModal = ref(false);
    const shareMessage = ref<MessageType | undefined>(undefined);
    const shareMessages = ref<MessageType[]>([]);

    // Multi-select State
    const isSelectionMode = ref(false);

    const openMenu = (msg: MessageType, style: any) => {
        activeMessage.value = msg;
        menuInlineStyle.value = style;
        showMenu.value = true;
    };

    const openShare = (msg?: MessageType) => {
        shareMessage.value = msg;
        showShareModal.value = true;
        showMenu.value = false;
    };

    const closeShare = () => {
        shareMessage.value = undefined;
        shareMessages.value = []
        showShareModal.value = false;
    };

    const toggleSelect = (msg: MessageType) => {
        const index = shareMessages.value.findIndex(m => m.id == msg.id);
console.log(msg, index)
        if (index !== -1) {
            // Nếu đã có trong mảng thì xóa đi
            shareMessages.value.splice(index, 1);

            // Kiểm tra nếu mảng trống thì tắt chế độ chọn
            if (shareMessages.value.length === 0) {
                isSelectionMode.value = false;
            }
        } else {
            // Nếu chưa có thì thêm vào mảng
            shareMessages.value.push(msg);
        }
    };

    const updateMenuStyle = (style: any) => {
        menuInlineStyle.value = style;
    };

    const cancelSelectionMode = () => {
        isSelectionMode.value = false;
        shareMessages.value = [];
    };

    return {
        activeMessage, showMenu, menuInlineStyle, openMenu,
        showShareModal, shareMessage, openShare,
        isSelectionMode, toggleSelect, updateMenuStyle
        , menuRef, closeShare,
        cancelSelectionMode,
        shareMessages
    };
});