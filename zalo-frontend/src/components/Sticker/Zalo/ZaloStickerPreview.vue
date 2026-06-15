<script setup lang="ts">
import { StickerItemType } from '@/types/entities';

defineProps<{
    sticker?: StickerItemType | null
}>()
</script>

<template>
    <Transition name="fade">
        <div v-if="sticker" class="fixed inset-0 z-50
            bg-black/70 backdrop-blur-sm
            flex items-center justify-center">
            <div class="w-60 h-60 rounded-3xl
                bg-zinc-900 border border-white/10
                flex items-center justify-center">
                <img :src="sticker.url" class="w-48 h-48 object-contain" />
            </div>
        </div>
    </Transition>
</template>