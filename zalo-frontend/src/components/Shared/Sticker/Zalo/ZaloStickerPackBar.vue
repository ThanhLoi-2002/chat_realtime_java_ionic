<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue';
import { StickerType } from '@/types/entities';
import { STICKER_URL } from '@/utils/constant';
import { oaStyle } from '@/assets/tailwindcss';
import { useStickerStore } from '@/stores/App/sticker.storage';

const props = defineProps<{
    packs: StickerType[]
    activePack?: StickerType
}>()

const emit = defineEmits<{
    select: [StickerType]
    openStore: []
    openRecent: []
    openAI: []
}>()

const navScrollRef = ref<HTMLElement | null>(null);
const stickerStor = useStickerStore()

// --- QUẢN LÝ TRẠNG THÁI CUỘN SIÊU MƯỢT ---
let isDown = false;
let startX = 0;
let scrollLeft = 0;
let isDragging = false;

// Hàm tự động cuộn icon được chọn vào CHÍNH GIỮA thanh bottom
const centerActiveIcon = (stickerId: number | string) => {
    nextTick(() => {
        const activeBtn = document.getElementById(`pack-btn-${stickerId}`);
        if (activeBtn && !isDown) { // Chỉ tự cuộn vào giữa khi người dùng không giữ kéo tay
            activeBtn.scrollIntoView({
                behavior: 'auto',
                block: 'nearest',
                inline: 'center'
            });
        }
    });
};

watch(() => props.activePack?.stickerId, (newId) => {
    if (newId) centerActiveIcon(newId);
});

// Xử lý mũi tên điều hướng
const handlePrevPack = () => {
    if (!props.packs.length || !props.activePack) return;
    const currentIndex = props.packs.findIndex(p => p.stickerId === props.activePack?.stickerId);
    if (currentIndex === 0) {
        emit('openRecent');
        centerActiveIcon('recent');
    } else if (currentIndex > 0) {
        const prevPack = props.packs[currentIndex - 1];
        emit('select', prevPack);
        centerActiveIcon(prevPack.stickerId);
    }
};

const handleNextPack = () => {
    if (!props.packs.length) return;
    if (!props.activePack) {
        const firstPack = props.packs[0];
        emit('select', firstPack);
        centerActiveIcon(firstPack.stickerId);
        return;
    }
    const currentIndex = props.packs.findIndex(p => p.stickerId === props.activePack?.stickerId);
    if (currentIndex < props.packs.length - 1) {
        const nextPack = props.packs[currentIndex + 1];
        emit('select', nextPack);
        centerActiveIcon(nextPack.stickerId);
    }
};

const onPackClick = (pack: StickerType) => {
    if (isDragging) return;
    emit('select', pack);
    centerActiveIcon(pack.stickerId);
};

const onRecentClick = () => {
    if (isDragging) return;
    emit('openRecent');
    centerActiveIcon('recent');
};

// --- 🌟 TỐI ƯU LĂN CHUỘT auto WHEEL ---
const handleWheel = (e: WheelEvent) => {
    if (!navScrollRef.value) return;
    e.preventDefault();

    // Sử dụng scrollBy mượt mà thay vì cộng trực tiếp pixel thô
    navScrollRef.value.scrollBy({
        left: e.deltaY * 0.8, // Nhân 0.8 giúp tốc độ lướt vừa vặn, không bị quá nhanh
        behavior: 'auto'    // Tạo hiệu ứng lướt quán tính êm ái
    });
};

// --- 🌟 TỐI ƯU KÉO CHUỘT KHÔNG BỊ KHỰNG (auto DRAG) ---
const handleMouseDown = (e: MouseEvent) => {
    if (!navScrollRef.value) return;
    isDown = true;
    isDragging = false;

    // TẬP TRUNG TỐI ƯU Ở ĐÂY: 
    // Gỡ bỏ tính năng snap và auto tạm thời để thanh cuộn bám khít tuyệt đối theo tay người dùng
    navScrollRef.value.classList.remove('snap-x', 'snap-mandatory', 'scroll-auto');

    startX = e.pageX - navScrollRef.value.offsetLeft;
    scrollLeft = navScrollRef.value.scrollLeft;
};

const handleMouseLeaveOrUp = () => {
    if (!isDown) return;
    isDown = false;

    if (navScrollRef.value) {
        // Sau khi người dùng buông tay: Khôi phục lại snap và auto để các nút bấm hoạt động bình thường
        navScrollRef.value.classList.add('snap-x', 'snap-mandatory', 'scroll-auto');
    }

    setTimeout(() => { isDragging = false; }, 50);
};

const handleMouseMove = (e: MouseEvent) => {
    if (!isDown || !navScrollRef.value) return;

    const x = e.pageX - navScrollRef.value.offsetLeft;
    const walk = (x - startX) * 1.8; // Tăng hệ số lên 1.8 giúp cảm giác vuốt "nhẹ" và bay hơn

    if (Math.abs(walk) > 5) {
        isDragging = true;
    }

    navScrollRef.value.scrollLeft = scrollLeft - walk;
};

onMounted(() => {
    if (navScrollRef.value) {
        navScrollRef.value.addEventListener('wheel', handleWheel, { passive: false });
    }
});

onUnmounted(() => {
    if (navScrollRef.value) {
        navScrollRef.value.removeEventListener('wheel', handleWheel);
    }
});
</script>

<template>
    <div :class="[oaStyle.bg.secondary, 'h-10 flex items-center rounded-b-md select-none relative shrink-0']">

        <div class="flex-1 h-full relative flex items-center overflow-hidden group">

            <button @click="handlePrevPack"
                :class="[
                    oaStyle.border.secondary, oaStyle.text.secondary, oaStyle.bg.hover1, oaStyle.bg.tertiary,
                    'absolute left-0 z-10 h-9 w-5 flex items-center justify-center rounded-r-md cursor-pointer opacity-0 group-hover:opacity-100 transition-opacity backdrop-blur-sm']">
                <i class="fas fa-chevron-left text-[10px]"></i>
            </button>

            <div ref="navScrollRef"
                class="w-full h-full flex items-center gap-1 overflow-x-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden snap-x snap-mandatory px-5 scroll-auto cursor-grab active:cursor-grabbing will-change-scroll"
                @mousedown="handleMouseDown" @mouseleave="handleMouseLeaveOrUp" @mouseup="handleMouseLeaveOrUp"
                @mousemove="handleMouseMove">

                <button id="pack-btn-recent" v-if="stickerStor.recentStickers.length > 0"
                    class="w-9 h-9 p-1.5 rounded-md flex items-center justify-center cursor-pointer text-gray-500 dark:text-gray-400 hover:bg-gray-300 dark:hover:bg-slate-700/50 shrink-0 transition-colors"
                    :class="[!activePack ? 'bg-gray-300 dark:bg-slate-700/80 text-blue-500' : '']"
                    @click="onRecentClick">
                    <i class="fas fa-clock"></i>
                </button>

                <!-- <button id="pack-btn-ai"
                    class="w-9 h-9 p-1.5 rounded-md flex items-center justify-center cursor-pointer text-gray-500 dark:text-gray-400 hover:bg-gray-300 dark:hover:bg-slate-700/50 shrink-0 transition-colors"
                    :class="[!activePack ? 'bg-gray-300 dark:bg-slate-700/80 text-blue-500' : '']"
                    @click="onAIClick">
                    <i class="fas fa-robot"></i>
                </button> -->

                <button v-for="pack in packs" :key="pack.stickerId" :id="`pack-btn-${pack.stickerId}`"
                    class="snap-center cursor-pointer w-9 h-9 p-1.5 rounded-md overflow-hidden transition-colors shrink-0 flex items-center justify-center"
                    :class="activePack?.stickerId === pack.stickerId
                        ? 'bg-gray-300 dark:bg-slate-700/80'
                        : 'hover:bg-gray-300/60 dark:hover:bg-slate-800/50'
                        " @click="onPackClick(pack)">
                    <img :src="STICKER_URL + pack.iconUrl" class="w-full h-full object-contain pointer-events-none"
                        loading="lazy" />
                </button>
            </div>

            <button @click="handleNextPack"
                :class="[
                    oaStyle.border.secondary, oaStyle.text.secondary, oaStyle.bg.hover1, oaStyle.bg.tertiary,
                    'absolute right-0 z-10 h-9 w-5 flex items-center justify-center rounded-l-md cursor-pointer opacity-0 group-hover:opacity-100 transition-opacity backdrop-blur-sm']">
                <i class="fas fa-chevron-right text-[10px]"></i>
            </button>
        </div>

        <button
            :class="[
                oaStyle.border.secondary, oaStyle.text.secondary, oaStyle.bg.hover1,
                'w-9 h-9 p-1.5 rounded-md flex items-center justify-center cursor-pointer shrink-0 border-l transition-colors']"
            @click="emit('openStore')">
            <i class="far fa-plus-square"></i>
        </button>

    </div>
</template>