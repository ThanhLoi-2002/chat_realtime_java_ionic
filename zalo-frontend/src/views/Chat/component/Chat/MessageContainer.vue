<template>

    <div class="flex-1 flex gap-2 items-start relative group w-full px-2"
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
        <div v-if="!actionStore.isSelectionMode" class="flex items-center gap-1
         opacity-0 transition my-auto" :class="[message.stt != -1 && 'group-hover:opacity-100']">
            <button @click.stop="setReplyingMessage(message)"
                class="px-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 dark:bg-gray-800 dark:text-white text-center cursor-pointer">
                <i class="fa-solid fa-quote-right text-[10px] leading-none"></i>
            </button>

            <button @click.stop="actionStore.openShare(message)"
                class="px-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 dark:bg-gray-800 dark:text-white text-center cursor-pointer">
                <i class="fas fa-share text-[10px] leading-none"></i>
            </button>

            <!-- MORE -->
            <button ref="moreBtnRef" @click.stop="toggleMenu"
                class="px-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 dark:bg-gray-800 dark:text-white text-center cursor-pointer">
                <i class="fas fa-ellipsis-h text-[10px] leading-none"></i>
            </button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useUserStore } from '@/stores/user.storage'
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue'
import { MemberRoleEnum, MessageEnum } from '@/types/enum';
import ImageMessage from '../Message/ImageMessage.vue';
import TextMessage from '../Message/TextMessage.vue';
import { MessageType } from '@/types/entities';
import Key from '@/components/Key/Key.vue';
import FileMessage from '../Message/FileMessage.vue';
import { useChatActionStore } from '@/composables/useChatAction';

const props = defineProps<{
    message: MessageType & any
    roles: Record<number, MemberRoleEnum> | undefined
    setReplyingMessage: (m: MessageType | null) => void
}>()

const userStorage = useUserStore()
const actionStore = useChatActionStore();

const isOwner = props.message.sender.id === userStorage.user?.id

// refs
const bubbleRef = ref<HTMLElement | null>(null)
const moreBtnRef = ref<HTMLElement | null>(null)

function setBubbleRef(el: HTMLElement | null) {
    bubbleRef.value = el
}

const toggleMenu = async () => {
    if (!actionStore.showMenu || actionStore.activeMessage?.id !== props.message.id) {
        // 1. Gửi lệnh đóng các menu khác và mở menu tin nhắn này ở trạng thái ẩn
        window.dispatchEvent(new CustomEvent('close-all-menus', { detail: props.message.id }));

        // Mở tàng hình để lấy menuRect
        actionStore.openMenu(props.message, {
            opacity: '0',
            position: 'fixed',
            pointerEvents: 'none',
            top: '0px',
            left: '0px'
        });

        // 2. Chờ Vue cập nhật DOM (lúc này MessageContextMenu ở file ngoài sẽ hiện lên với opacity 0)
        await nextTick();

        // 3. Tính toán vị trí dựa trên menu đã có trong DOM
        const calculatedStyle = positionMenu();

        if (calculatedStyle) {
            // 4. Update style cuối cùng để menu hiển thị đúng chỗ
            actionStore.updateMenuStyle(calculatedStyle);
        }
    } else {
        actionStore.showMenu = false;
    }
};

// calculate position and set menuInlineStyle
const positionMenu = () => {
    // THAY ĐỔI: Lấy trực tiếp từ actionStore thay vì contextMenuComponentRef
    const menuElement = actionStore.menuRef;

    if (!bubbleRef.value || !menuElement) return null;

    const bubbleRect = bubbleRef.value.getBoundingClientRect();
    const menuRect = menuElement.getBoundingClientRect();
    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;
    const margin = 8;

    let top = bubbleRect.bottom + margin;
    let left: number;

    if (isOwner) {
        left = bubbleRect.right - menuRect.width;
    } else {
        left = bubbleRect.left;
    }

    // Clamp logic giữ nguyên code của bạn
    if (left + menuRect.width > viewportWidth - margin) left = viewportWidth - menuRect.width - margin;
    if (left < margin) left = margin;

    if (top + menuRect.height > viewportHeight - margin) {
        const aboveTop = bubbleRect.top - margin - menuRect.height;
        if (aboveTop >= margin) top = aboveTop;
        else top = Math.max(margin, viewportHeight - menuRect.height - margin);
    }

    return {
        position: 'fixed',
        top: `${Math.round(top)}px`,
        left: `${Math.round(left)}px`,
        opacity: '1',
        zIndex: '9999', // Đảm bảo nổi lên trên cùng
        pointerEvents: 'auto'
    };
}

const handleClickOutside = (e: MouseEvent) => {
    const target = e.target as Node;

    // 1. Nếu menu đang đóng, không làm gì cả
    if (!actionStore.showMenu || actionStore.activeMessage?.id !== props.message.id) return;

    // 2. Kiểm tra nếu click vào chính cái Menu đang mở (lấy từ Store)
    if (actionStore.menuRef && actionStore.menuRef.contains(target)) return;

    // 3. Kiểm tra nếu click vào nút "More" (dấu 3 chấm) của tin nhắn này
    if (moreBtnRef.value && moreBtnRef.value.contains(target)) return;

    // 4. Nếu click vào vùng bubble của chính nó (nhưng không phải nút more) -> Đóng
    if (bubbleRef.value && bubbleRef.value.contains(target)) {
        actionStore.showMenu = false;
        return;
    }

    // 5. Click ra bất cứ đâu khác -> Đóng
    actionStore.showMenu = false;
};

const handleScrollOrResize = () => {
    // Nếu menu của tin nhắn này đang mở thì mới tính lại vị trí
    if (actionStore.showMenu && actionStore.activeMessage?.id === props.message.id) {
        nextTick(() => {
            const style = positionMenu();
            if (style) actionStore.updateMenuStyle(style);
        });
    }
};

// Hàm này không còn quá cần thiết nếu bạn dùng Store tập trung, 
// nhưng giữ lại để hỗ trợ các sự kiện custom khác nếu có
const handleGlobalMenuClose = (e: any) => {
    if (e.detail !== props.message.id) {
        actionStore.showMenu = false;
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