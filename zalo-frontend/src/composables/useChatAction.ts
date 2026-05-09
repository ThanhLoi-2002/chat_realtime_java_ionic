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
    const shareMessage = ref<MessageType | null>(null);

    // Multi-select State
    const isSelectionMode = ref(false);
    const selectedIds = ref<Set<number>>(new Set());

    const openMenu = (msg: MessageType, style: any) => {
        activeMessage.value = msg;
        menuInlineStyle.value = style;
        showMenu.value = true;
    };

    const openShare = (msg: MessageType) => {
        shareMessage.value = msg;
        showShareModal.value = true;
        showMenu.value = false;
    };

    const closeShare = () => {
        shareMessage.value = null;
        showShareModal.value = false;
    };

    const toggleSelect = (id: number) => {
        if (selectedIds.value.has(id)) {
            selectedIds.value.delete(id);
            if (selectedIds.value.size === 0) isSelectionMode.value = false;
        } else {
            selectedIds.value.add(id);
        }
    };

    const updateMenuStyle = (style: any) => {
        menuInlineStyle.value = style;
    };

    const cancelSelectionMode = () => {
        isSelectionMode.value = false;
        selectedIds.value.clear();
    };

    return {
        activeMessage, showMenu, menuInlineStyle, openMenu,
        showShareModal, shareMessage, openShare,
        isSelectionMode, selectedIds, toggleSelect, updateMenuStyle
        , menuRef, closeShare,
        cancelSelectionMode
    };
});