<script setup lang="ts">
import { computed, onMounted, ref } from "vue";

import { useTranslate } from "@/composables/useTranslate.ts";
import { StickerItemType, StickerType } from "@/types/entities.ts";
import { useStickerStore } from "@/stores/sticker.storage.ts";
import ZaloStickerGrid from "./ZaloStickerGrid.vue";
import ZaloStickerPackBar from "./ZaloStickerPackBar.vue";
import ZaloStickerPreview from "./ZaloStickerPreview.vue";
import { style } from "@/assets/tailwindcss.ts";
import { useClickOutside } from "@/composables/useClickOutside.ts";

const emit = defineEmits<{
    (e: 'close'): void
}>();

const { t } = useTranslate();
const stickerStorage = useStickerStore();

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
        s.name.toLowerCase().includes(keyword.value.toLowerCase()),
    );
});

const handleSelectSticker = (sticker: StickerItemType) => {
    stickerStorage.addRecentSticker(sticker);

    console.log("send sticker", sticker);
};

onMounted(() => {
    stickerStorage.getAll();
});
</script>

<template>
    <div ref="pickerRef"
        class="absolute w-90 h-120 bottom-full left-4 mb-2 p-4 rounded-md overflow-hidden bg-zinc-900/95 backdrop-blur-2xl border border-white/10 shadow-2xl text-white flex flex-col">
        <!-- Search -->

        <div class="p-3">
            <input v-model="keyword" placeholder="Tìm kiếm sticker"
                class="w-full h-11 rounded-full bg-zinc-800/80 px-4 text-sm outline-none border border-white/5" />
        </div>

        <!-- Recent -->
        <div v-if="stickerStorage.recentStickers.length" class="px-4 text-sm text-zinc-400">
            {{ t("recent") }}
        </div>

        <ZaloStickerGrid v-if="stickerStorage.recentStickers.length" :stickers="stickerStorage.recentStickers"
            @select="handleSelectSticker" @preview="previewSticker = $event" />

        <!-- Main -->
        <div class="flex-1 overflow-y-auto">
            <div v-for="sticker in stickerStorage.stickers" :key="sticker.stickerId" class="flex flex-col gap-2">
                <div class="font-weight-400">{{ sticker.name }}</div>
                <ZaloStickerGrid :stickers="sticker.items" @select="handleSelectSticker"
                    @preview="previewSticker = $event" />
                <hr :class="[style.bg.softHover]">
            </div>
        </div>

        <!-- Bottom bar -->
        <div class="pb-[env(safe-area-inset-bottom)]">
            <ZaloStickerPackBar :packs="stickerStorage.stickers" :active-pack="activePack"
                @select="activePack = $event" />
        </div>

        <!-- Preview -->
        <ZaloStickerPreview :sticker="previewSticker" @click="previewSticker = null" />
    </div>
</template>
