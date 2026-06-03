<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from "vue";

import { useTranslate } from "@/composables/useTranslate.ts";
import { StickerItemType, StickerType } from "@/types/entities.ts";
import { useStickerStore } from "@/stores/sticker.storage.ts";
import ZaloStickerGrid from "./ZaloStickerGrid.vue";
import ZaloStickerPackBar from "./ZaloStickerPackBar.vue";
import ZaloStickerPreview from "./ZaloStickerPreview.vue";
import { style } from "@/assets/tailwindcss.ts";
import { useClickOutside } from "@/composables/useClickOutside.ts";
import EmojiPicker from "@/components/Emoji/EmojiPicker.vue";
import { normalizeText } from "@/utils/helper.ts";

const emit = defineEmits(['close', "handleSelectEmoji"]);

const { t } = useTranslate();
const stickerStorage = useStickerStore();
// 2. Khai báo ref trỏ tới khung cuộn (đặt tên trùng với ref ở template)
const scrollContainerRef = ref<HTMLElement | null>(null);

// --- LOGIC CHUYỂN TAB ---
type TabType = 'sticker' | 'emoji';
const activeTab = ref<TabType>('sticker'); // Mặc định mở tab sticker trước

const activePack = ref<StickerType | undefined>(stickerStorage.stickers[0] || undefined);

const keyword = ref("");

const previewSticker = ref<StickerItemType | null>(null);

// 3. Tạo một template ref để trỏ tới khung picker
const pickerRef = ref<HTMLElement | null>(null);

// 4. Áp dụng composable: Khi click ra ngoài pickerRef, kích hoạt emit('close')
useClickOutside(pickerRef, () => {
    emit('close');
});

const stickers = computed(() => {
    return stickerStorage.stickers.filter((s) =>
        normalizeText(s.name).includes(normalizeText(keyword.value)),
    );
});

const handleSelectSticker = (sticker: StickerItemType) => {
    stickerStorage.addRecentSticker(sticker);

    console.log("send sticker", sticker);
};

// 3. Đặt watch để bắt sự kiện mỗi khi người dùng gõ tìm kiếm
watch(keyword, () => {
    // nextTick đảm bảo Vue đã render xong giao diện mới sau khi lọc rồi mới cuộn
    nextTick(() => {
        if (scrollContainerRef.value) {
            scrollContainerRef.value.scrollTop = 0; // Đẩy thanh cuộn lên đầu top 0

            // Hoặc nếu muốn cuộn mượt mà (smooth), bạn có thể dùng dòng dưới:
            // scrollContainerRef.value.scrollTo({ top: 0, behavior: 'smooth' });
        }
    });
});

onMounted(() => {
    stickerStorage.getAll();
});
</script>

<template>
    <div ref="pickerRef"
        class="absolute w-80 h-100 bottom-full left-4 mb-2 p-4 rounded-md overflow-hidden bg-gray-400 dark:bg-slate-800/50 backdrop-blur-2xl border border-slate-500/50 dark:border-white/10 shadow-2xl text-white flex flex-col">

        <div class="flex gap-2 border-b border-gray-300 dark:border-slate-700 mb-2 pb-1 text-xs font-medium shrink-0">
            <button @click="activeTab = 'sticker'"
                :class="['flex-1 py-1 text-center transition-all rounded-md cursor-pointer', activeTab === 'sticker' ? 'bg-blue-500 text-white font-semibold' : 'text-gray-500 dark:text-gray-400 hover:bg-gray-300 dark:hover:bg-slate-700/50']">
                Sticker
            </button>
            <button @click="activeTab = 'emoji'"
                :class="['flex-1 py-1 text-center transition-all rounded-md cursor-pointer', activeTab === 'emoji' ? 'bg-blue-500 text-white font-semibold' : 'text-gray-500 dark:text-gray-400 hover:bg-gray-300 dark:hover:bg-slate-700/50']">
                Emoji
            </button>
        </div>

        <div v-if="activeTab == 'sticker'" class="flex flex-col flex-1 overflow-hidden">

            <div class="py-1 shrink-0">
                <input v-model="keyword" :placeholder="t('search')"
                    class="w-full h-6 rounded-full dark:bg-slate-700/50 px-4 text-xs outline-none border border-white/5" />
            </div>

            <div ref="scrollContainerRef" class="flex-1 overflow-y-auto pr-1">
                <div v-if="stickerStorage.recentStickers.length && !keyword" class="shrink-0">
                    <div class="font-weight-400 text-sm mt-2">
                        {{ t("recent") }}
                    </div>
                    <ZaloStickerGrid :stickers="stickerStorage.recentStickers" @select="handleSelectSticker"
                        @preview="previewSticker = $event" :sticker-size="60" />
                </div>

                <div v-for="sticker in stickers" :key="sticker.stickerId" class="flex flex-col gap-1">
                    <div class="font-weight-400 text-sm mt-2">{{ sticker.name }}</div>
                    <ZaloStickerGrid :stickers="sticker.items" @select="handleSelectSticker"
                        @preview="previewSticker = $event" :sticker-size="60" />
                </div>
            </div>

            <div class="pt-2 border-t border-slate-500/20 shrink-0">
                <ZaloStickerPackBar :packs="stickerStorage.stickers" :active-pack="activePack"
                    @select="activePack = $event" />
            </div>

            <ZaloStickerPreview :sticker="previewSticker" @click="previewSticker = null" />
        </div>

        <div v-else class="flex-1 overflow-hidden">
            <EmojiPicker @select="emit('handleSelectEmoji')" />
        </div>

    </div>
</template>
