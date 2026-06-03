<script setup lang="ts">

import { StickerItemType } from '@/types/entities.ts';
import ZaloSticker from './ZaloSticker.vue';

defineProps<{
    stickers: StickerItemType[]
    stickerSize: number
}>()

const emit = defineEmits<{
    select: [StickerItemType]
    preview: [StickerItemType]
}>()
</script>

<template>
    <div class="grid grid-cols-4 gap-2 p-3">
        <ZaloSticker v-for="item in stickers" :key="item.id" :stickerItem="item" :size="stickerSize" @select="emit('select', $event)" @preview="emit('preview', $event)" />
    </div>
</template>