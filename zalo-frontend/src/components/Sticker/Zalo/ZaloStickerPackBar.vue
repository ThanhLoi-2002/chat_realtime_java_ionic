<script setup lang="ts">
import { StickerType } from '@/types/entities';
import { STICKER_URL } from '@/utils/constant';

const props = defineProps<{
    packs: StickerType[]
    activePack?: StickerType
}>()

const emit = defineEmits<{
    select: [StickerType]
}>()
// console.log(props.packs)
</script>

<template>
    <div class="h-12
        overflow-x-auto
        flex gap-2 px-2 items-center
        snap-x snap-mandatory">
        <button v-for="pack in packs" :key="pack.stickerId" class="snap-start cursor-pointer
            min-w-8 h-8 rounded-md
            overflow-hidden transition" :class="activePack?.stickerId === pack.stickerId
                    ? 'ring-1 ring-sky-400'
                    : ''
                " @click="emit('select', pack)">
            <img :src="STICKER_URL + pack.iconUrl" class="w-full h-full object-cover" loading="lazy"/>
        </button>
    </div>
</template>