<script setup lang="ts">
import { StickerType } from '@/types/entities';

const props = defineProps<{
    packs: StickerType[]
    activePack?: StickerType
}>()

const emit = defineEmits<{
    select: [StickerType]
}>()
console.log(props.packs)
</script>

<template>
    <div class="h-16
        overflow-x-auto
        flex gap-2 px-2
        snap-x snap-mandatory">
        <button v-for="pack in packs" :key="pack.stickerId" class="snap-start
            min-w-12 h-12 rounded-2xl
            overflow-hidden transition" :class="activePack?.stickerId === pack.stickerId
                    ? 'ring-2 ring-sky-400'
                    : ''
                " @click="emit('select', pack)">
            <img :src="pack.iconUrl" class="w-full h-full object-cover" />
        </button>
    </div>
</template>