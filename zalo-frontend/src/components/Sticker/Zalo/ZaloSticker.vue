<template>
    <div class="sticker" :style="{
        width: `${size}px`,
        height: `${size}px`,
        backgroundImage: `url(${stickerItem.url})`,
        backgroundPosition: `${positionX}px 0px`,
        backgroundRepeat: 'repeat-x',
        backgroundSize: `${stickerItem.frameCount * size}px ${size}px`
    }" />
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { StickerItemType } from "@/types/entities"

const props = defineProps<{
    stickerItem: StickerItemType
    size: number
}>()

const emit = defineEmits<{
    select: [StickerItemType]
    preview: [StickerItemType]
}>()

const currentFrame = ref(0)
const positionX = ref(0)

const timer = ref<ReturnType<typeof setInterval> | null>(null)

onMounted(() => {
    timer.value = setInterval(() => {
        currentFrame.value = (currentFrame.value + 1) % props.stickerItem.frameCount
        positionX.value = -(currentFrame.value * props.size)
    }, props.stickerItem.frameCount * 10)
})

onUnmounted(() => {
    if (timer.value) {
        clearInterval(timer.value)
    }
})
</script>

<style scoped>
.sticker {
    overflow: hidden;
}
</style>