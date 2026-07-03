<script setup lang="ts">
import { StickerItemType } from '@/types/entities';
import ZaloSticker from './ZaloSticker.vue';
import { oaStyle } from '@/assets/tailwindcss';

defineProps<{
    sticker?: StickerItemType | null
}>()
</script>

<template>
    <Transition name="fade">
        <div v-if="sticker" :class="[
            oaStyle.bg.secondary,
            'fixed inset-0 z-50 backdrop-blur-sm flex items-center justify-center']">
            <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'w-60 h-60 rounded-3xl border flex items-center justify-center']">
                <ZaloSticker :sticker-item="sticker" :is-hover="false" :size="200"/>
            </div>
        </div>
    </Transition>
</template>