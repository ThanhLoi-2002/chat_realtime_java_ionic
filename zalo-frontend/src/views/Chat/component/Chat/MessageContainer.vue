<template>

    <div class="flex gap-2 items-start relative group w-full px-6"
        :class="[isOwner ? 'ml-auto flex-row-reverse' : '', message.reactions?.length > 0 && 'mb-4']" ref="rootRef"
        :id="`msg-${message.id}`">

        <!-- AVATAR -->
        <div v-if="!isOwner && message.showAvatar" class="relative shrink-0">
            <circle-avatar :user="message.sender" size="size-10" />
            <key v-if="roles" :role="roles[message?.sender?.id]" />
        </div>

        <div v-else-if="!isOwner" class="px-5 py-2"></div>

        <!-- BUBBLE -->
        <div class="relative group"> <!-- thêm class group ở đây -->
            <image-message :message="message" :setBubbleRef="setBubbleRef"
                v-if="message.contentType == MessageEnum.IMAGE" :isOwner="isOwner"
                :role="roles ? roles[message.sender?.id] : undefined" />

            <text-message :message="message" :setBubbleRef="setBubbleRef"
                v-if="message.contentType == MessageEnum.TEXT || message.contentType == null" :isOwner="isOwner"
                :role="roles ? roles[message.sender?.id] : undefined" />

            <file-message :message="message" :setBubbleRef="setBubbleRef" v-if="message.contentType == MessageEnum.FILE"
                :isOwner="isOwner" :role="roles ? roles[message.sender?.id] : undefined" :showReaction="true" />
        </div>

        <!-- ACTIONS -->
        <div class="flex items-center gap-1
         opacity-0 transition my-auto" :class="[message.stt != -1 && 'group-hover:opacity-100']">
            <button ref="moreBtnRef" @click.stop="setReplyingMessage(message)"
                class="px-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 dark:bg-gray-800 dark:text-white text-center cursor-pointer">
                <i class="fa-solid fa-quote-right text-[10px] leading-none"></i>
            </button>

            <button ref="moreBtnRef" @click.stop="openShareModal"
                class="px-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 dark:bg-gray-800 dark:text-white text-center cursor-pointer">
                <i class="fas fa-share text-[10px] leading-none"></i>
            </button>

            <!-- MORE -->
            <button ref="moreBtnRef" @click.stop="toggleMenu"
                class="px-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 dark:bg-gray-800 dark:text-white text-center cursor-pointer">
                <i class="fas fa-ellipsis-h text-[10px] leading-none"></i>
            </button>
        </div>

        <!-- Teleported CONTEXT MENU (rendered into body) -->
        <teleport to="body">
            <message-context-menu 
                :message="message" 
                v-model:showMenu="showMenu" 
                v-model:showConfirm="showConfirm"
                :menuInlineStyle="menuInlineStyle" 
                ref="contextMenuComponentRef" />
        </teleport>

        <modal ref="shareModal" :title="t('share')">
            <share-message-u-i :message="message" />
        </modal>

        <!-- <confirm-modal v-model:showConfirm="showConfirm" :onOk="onDelete" :message="t('deleteMessage')"
            :header="t('deleteMessage')" /> -->
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useUserStore } from '@/stores/user.storage'
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue'
import { useTranslate } from '@/composables/useTranslate';
import { MemberRoleEnum, MessageEnum } from '@/types/enum';
import { useMessageStore } from '@/stores/message.storage';
import ImageMessage from '../Message/ImageMessage.vue';
import TextMessage from '../Message/TextMessage.vue';
import { MessageType } from '@/types/entities';
import Key from '@/components/Key/Key.vue';
import ConfirmModal from '@/components/Modal/ConfirmModal.vue';
import FileMessage from '../Message/FileMessage.vue';
import Modal from '@/components/Modal/Modal.vue';
import ShareMessageUI from '../Message/ShareMessageUI.vue';
import MessageContextMenu from '../Message/MessageContextMenu.vue';

const props = defineProps<{
    message: MessageType & any
    roles: Record<number, MemberRoleEnum> | undefined
    setReplyingMessage: (m: MessageType | null) => void
}>()

const { t } = useTranslate()

const userStorage = useUserStore()
const shareModal = ref()

const isOwner = props.message.sender.id === userStorage.user?.id

// refs
const bubbleRef = ref<HTMLElement | null>(null)
const moreBtnRef = ref<HTMLElement | null>(null)
const contextMenuComponentRef = ref();
const showConfirm = ref(false)

// menu state
const showMenu = ref(false)
const menuInlineStyle = ref<Record<string, string>>({ opacity: '0' })

function setBubbleRef(el: HTMLElement | null) {
    bubbleRef.value = el
}

const openShareModal = () => {
    shareModal.value.present()
}

const toggleMenu = async () => {
    if (!showMenu.value) {
        // Trước khi mở, gửi thông báo đóng tất cả các menu khác
        // Bạn có thể dùng mitt hoặc window.dispatchEvent
        window.dispatchEvent(new CustomEvent('close-all-menus', { detail: props.message.id }));

        showMenu.value = true;
        await nextTick();
        positionMenu();
        requestAnimationFrame(() => {
            menuInlineStyle.value.opacity = '1';
        });
    } else {
        showMenu.value = false;
    }
}

// calculate position and set menuInlineStyle
const positionMenu = () => {
    // Lấy element thực tế từ expose của con
    const menuElement = contextMenuComponentRef.value?.menuRef;

    if (!bubbleRef.value || !menuElement) return

    const bubbleRect = bubbleRef.value.getBoundingClientRect()
    const menuRect = menuElement?.getBoundingClientRect()
    const viewportWidth = window.innerWidth
    const viewportHeight = window.innerHeight
    const margin = 8

    // default position: place menu below the bubble (align start or end depending on isOwner)
    let top = bubbleRect.bottom + margin
    let left: number
    if (isOwner) {
        // align menu's right edge to bubble's right edge
        left = bubbleRect.right - menuRect.width
    } else {
        // align menu's left edge to bubble's left edge
        left = bubbleRect.left
    }

    // If would overflow right, clamp
    if (left + menuRect.width > viewportWidth - 8) {
        left = viewportWidth - menuRect.width - 8
    }

    // If would overflow left, clamp
    if (left < 8) {
        left = 8
    }

    // If not enough space below, try place above
    if (top + menuRect.height > viewportHeight - 8) {
        const aboveTop = bubbleRect.top - margin - menuRect.height
        if (aboveTop >= 8) {
            top = aboveTop
        } else {
            // cannot fit above or below well, clamp top
            top = Math.max(8, viewportHeight - menuRect.height - 8)
        }
    }

    menuInlineStyle.value = {
        position: 'fixed',
        top: `${Math.round(top)}px`,
        left: `${Math.round(left)}px`,
        opacity: menuInlineStyle.value.opacity ?? '1'
    }
}

// click outside: if clicked outside menu and outside more button, close
const handleClickOutside = (e: MouseEvent) => {
    const target = e.target as Node

    if (contextMenuComponentRef.value.menuRef && contextMenuComponentRef.value.menuRef.contains(target)) return
    if (moreBtnRef.value && moreBtnRef.value.contains(target)) return

    // clicking inside bubble but not on more button should close menu
    if (bubbleRef.value && bubbleRef.value.contains(target)) {
        showMenu.value = false
        return
    }

    showMenu.value = false
}

const handleScrollOrResize = () => {
    if (showMenu.value) nextTick(positionMenu)
}

const handleGlobalMenuClose = (e: any) => {
    // Nếu ID nhận được khác với ID của tin nhắn này, thì đóng menu
    if (e.detail !== props.message.id) {
        showMenu.value = false;
    }
};

onMounted(() => {
    document.addEventListener('click', handleClickOutside)
    window.addEventListener('resize', handleScrollOrResize)
    window.addEventListener('scroll', handleScrollOrResize, true) // capture scroll on ancestors
    window.addEventListener('close-all-menus', handleGlobalMenuClose);
})

onBeforeUnmount(() => {
    document.removeEventListener('click', handleClickOutside)
    window.removeEventListener('resize', handleScrollOrResize)
    window.removeEventListener('scroll', handleScrollOrResize, true)
    window.removeEventListener('close-all-menus', handleGlobalMenuClose);
})
</script>