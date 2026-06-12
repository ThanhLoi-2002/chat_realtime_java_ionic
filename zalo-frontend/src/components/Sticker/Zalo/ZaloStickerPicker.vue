<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from "vue";

import { useTranslate } from "@/composables/useTranslate.ts";
import { StickerItemType, StickerType } from "@/types/entities.ts";
import { useStickerStore } from "@/stores/sticker.storage.ts";
import ZaloStickerGrid from "./ZaloStickerGrid.vue";
import ZaloStickerPackBar from "./ZaloStickerPackBar.vue";
import ZaloStickerPreview from "./ZaloStickerPreview.vue";
import { useClickOutside } from "@/composables/useClickOutside.ts";
import EmojiPicker from "@/components/Emoji/EmojiPicker.vue";
import { normalizeText } from "@/utils/helper.ts";
import { SendStickerType } from "@/types/common.ts";
import { useConversationStore } from "@/stores/conversation.storage.ts";

const props = defineProps<{
    scrollToBottom: () => void
}>()

const emit = defineEmits(['close', "handleSelectEmoji"]);

const { t } = useTranslate();
const stickerStorage = useStickerStore();
const convStorage = useConversationStore()

const scrollContainerRef = ref<HTMLElement | null>(null);

type TabType = 'sticker' | 'emoji';
const activeTab = ref<TabType>('sticker');

const activePack = ref<StickerType | undefined>(undefined);
const keyword = ref("");
const previewSticker = ref<StickerItemType | null>(null);
const pickerRef = ref<HTMLElement | null>(null);

useClickOutside(pickerRef, () => {
    emit('close');
});

const stickers = computed(() => {
    return stickerStorage.stickers.filter((s) =>
        normalizeText(s.name).includes(normalizeText(keyword.value)),
    );
});

// --- LOGIC CUỘN ĐẾN PACK ĐƯỢC CHỌN ---
const handlePackSelect = (pack: StickerType) => {
    activePack.value = pack;

    // Tìm thẻ div đại diện của pack dựa trên ID gắn ở template
    const targetElement = document.getElementById(`pack-section-${pack.stickerId}`);

    if (targetElement && scrollContainerRef.value) {
        // Tính toán khoảng cách chính xác từ đỉnh khung cuộn hiện tại đến phần tử đích
        const targetTop = targetElement.offsetTop - scrollContainerRef.value.offsetTop;

        // Thực hiện cuộn mượt mà
        scrollContainerRef.value.scrollTo({
            top: targetTop,
            behavior: "smooth"
        });
    }
};

const handleRecentSelect = () => {
    activePack.value = undefined;

    // Tìm thẻ div đại diện của pack dựa trên ID gắn ở template
    const targetElement = document.getElementById(`pack-section-recent`);

    if (targetElement && scrollContainerRef.value) {
        // Tính toán khoảng cách chính xác từ đỉnh khung cuộn hiện tại đến phần tử đích
        const targetTop = targetElement.offsetTop - scrollContainerRef.value.offsetTop;

        // Thực hiện cuộn mượt mà
        scrollContainerRef.value.scrollTo({
            top: targetTop,
            behavior: "smooth"
        });
    }
};

const sendSticker = async (sticker: StickerItemType) => {
    const payload: SendStickerType = {
        conversationId: convStorage.conversation?.id,
        sticker
    }

    const success = await stickerStorage.sendSticker(payload);

    if (success) {
        emit('close')
        props.scrollToBottom();
    }
}

const scrollToPack = async (pack: StickerType) => {
    activePack.value = pack;

    await nextTick();

    const targetElement = document.getElementById(
        `pack-section-${pack.stickerId}`
    );

    if (!targetElement || !scrollContainerRef.value) return;

    const targetTop =
        targetElement.offsetTop -
        scrollContainerRef.value.offsetTop;

    // scrollContainerRef.value.scrollTop = targetTop;

    scrollContainerRef.value.scrollTo({
        top: targetTop,
        behavior: "auto"
    });
};

// Thêm hàm theo dõi vị trí cuộn để đồng bộ ngược lại thanh đáy
const handleScrollSpy = (event: Event) => {
    // Nếu đang tìm kiếm bằng từ khóa thì không chạy tính năng đồng bộ cuộn tránh xung đột
    if (keyword.value) return;

    const container = event.target as HTMLElement;
    const currentScrollTop = container.scrollTop;

    // 1. Kiểm tra vùng "Gần đây" trước nếu có tồn tại
    const recentSection = document.getElementById('pack-section-recent');
    if (recentSection) {
        const recentTop = recentSection.offsetTop - container.offsetTop;
        if (currentScrollTop >= recentTop && currentScrollTop < recentTop + recentSection.offsetHeight) {
            activePack.value = undefined; // Sáng đèn nút đồng hồ
            return;
        }
    }

    // 2. Kiểm tra các pack sticker thông thường
    for (const pack of stickerStorage.stickers) {
        const section = document.getElementById(`pack-section-${pack.stickerId}`);
        if (section) {
            const sectionTop = section.offsetTop - container.offsetTop;
            // Cho phép lệch 40px sai số tiêu đề để mang lại cảm giác phản hồi nhanh nhạy
            if (currentScrollTop >= sectionTop - 40 && currentScrollTop < sectionTop + section.offsetHeight - 40) {
                activePack.value = pack; // Đổi trạng thái active
                break;
            }
        }
    }
};

const handleSelectSticker = (sticker: StickerItemType) => {
    stickerStorage.addRecentSticker(sticker);
    sendSticker(sticker)
};

watch(keyword, () => {
    nextTick(() => {
        if (scrollContainerRef.value) {
            scrollContainerRef.value.scrollTop = 0;
        }
    });
});

onMounted(async () => {
    await stickerStorage.getAll();

    if (stickerStorage.pointToStickerId) {
        const targetPack = stickerStorage.stickers.find(
            pack =>
                pack.stickerId ===
                stickerStorage.pointToStickerId
        );

        if (targetPack) {
            await scrollToPack(targetPack);
            return;
        }
    }

    if (stickerStorage.stickers.length > 0) {
        activePack.value = stickerStorage.stickers[0];
    }
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

            <div ref="scrollContainerRef" class="flex-1 overflow-y-auto pr-1 scroll-auto" @scroll="handleScrollSpy">
                <div v-if="stickerStorage.recentStickers.length && !keyword" class="shrink-0">
                    <div class="font-weight-400 text-sm mt-2" :id="`pack-section-recent`">
                        {{ t("recent") }}
                    </div>
                    <ZaloStickerGrid :stickers="stickerStorage.recentStickers" @select="handleSelectSticker"
                        @preview="previewSticker = $event" :sticker-size="60" />
                </div>

                <div v-for="sticker in stickers" :key="sticker.stickerId" :id="`pack-section-${sticker.stickerId}`"
                    class="flex flex-col gap-1">
                    <div class="font-weight-400 text-sm mt-2">{{ sticker.name }}</div>
                    <ZaloStickerGrid :stickers="sticker.items" @select="handleSelectSticker"
                        @preview="previewSticker = $event" :sticker-size="60" />
                </div>
            </div>

            <div class="pt-2 border-t border-slate-500/20 shrink-0">
                <ZaloStickerPackBar :packs="stickerStorage.stickers" :active-pack="activePack"
                    @select="handlePackSelect" @open-recent="handleRecentSelect" />
            </div>

            <ZaloStickerPreview :sticker="previewSticker" @click="previewSticker = null" />
        </div>

        <div v-else class="flex-1 overflow-hidden">
            <EmojiPicker @select="(emoji: any) => emit('handleSelectEmoji', emoji)" />
        </div>

    </div>
</template>