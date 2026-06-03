<script setup lang="ts">
import { StickerType } from '@/types/entities';
import { STICKER_URL } from '@/utils/constant';

const props = defineProps<{
    packs: StickerType[]
    activePack?: StickerType
}>()

const emit = defineEmits<{
    select: [StickerType]
    openStore: []       // Emit khi bấm vào dấu cộng để mở cửa hàng sticker
    openRecent: []      // Emit khi bấm vào biểu tượng đồng hồ để xem sticker gần đây
}>()
</script>

<template>
    <div
        class="h-10 flex items-center gap-1 bg-gray-200 dark:bg-slate-900/40 rounded-b-md select-none relative shrink-0">

        <button
            class="w-9 h-9 p-1.5 rounded-md flex items-center justify-center cursor-pointer text-gray-500 dark:text-gray-400 hover:bg-gray-300 dark:hover:bg-slate-700/50 shrink-0 transition-colors"
            :class="[!activePack ? 'bg-gray-300 dark:bg-slate-700/80'
                : 'hover:bg-gray-300/60 dark:hover:bg-slate-800/50']" @click="emit('openRecent')">
            <i class="fas fa-clock"></i>
        </button>

        <div
            class="flex-1 h-full flex items-center gap-1 overflow-x-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden snap-x snap-mandatory px-1">
            <button v-for="pack in packs" :key="pack.stickerId"
                class="snap-start cursor-pointer w-9 h-9 p-1.5 rounded-md overflow-hidden transition-colors shrink-0 flex items-center justify-center"
                :class="activePack?.stickerId === pack.stickerId
                    ? 'bg-gray-300 dark:bg-slate-700/80'
                    : 'hover:bg-gray-300/60 dark:hover:bg-slate-800/50'
                    " @click="emit('select', pack)">
                <img :src="STICKER_URL + pack.iconUrl" class="w-full h-full object-contain" loading="lazy" />
            </button>
        </div>

        <button
            class="w-9 h-9 p-1.5 rounded-md flex items-center justify-center cursor-pointer text-gray-500 dark:text-gray-400 hover:bg-gray-300 dark:hover:bg-slate-700/50 shrink-0 border-l border-gray-300 dark:border-slate-700/50 transition-colors"
            @click="emit('openStore')">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2"
                stroke="currentColor" class="w-5 h-5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
        </button>

    </div>
</template>