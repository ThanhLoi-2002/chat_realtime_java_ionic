<template>
    <div ref="stickerRef" 
         class="overflow-hidden cursor-pointer dark:hover:bg-slate-700 rounded-lg" 
         :style="stickerStyle"
         @click="emit('select', props.stickerItem)" />
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { StickerItemType } from "@/types/entities"
import { STICKER_URL } from '@/utils/constant'

const props = defineProps<{
    stickerItem: StickerItemType
    size: number
}>()

const emit = defineEmits<{
    select: [StickerItemType]
    preview: [StickerItemType]
}>()

// State quản lý animation và lazy load
const stickerRef = ref<HTMLDivElement | null>(null)
const isVisible = ref(false) // Trạng thái sticker có nằm trong khung nhìn hay không
const isLoaded = ref(false)  // Trạng thái đã từng lọt vào màn hình (để giữ ảnh lại)

const currentFrame = ref(0)
const positionX = ref(0)
const timer = ref<ReturnType<typeof setInterval> | null>(null)

// Hàm khởi động animation (chỉ chạy khi nhìn thấy)
const startAnimation = () => {
    if (timer.value) return
    timer.value = setInterval(() => {
        currentFrame.value = (currentFrame.value + 1) % props.stickerItem.frameCount
        positionX.value = -(currentFrame.value * props.size)
    }, props.stickerItem.frameCount * 10) // Tốc độ frame dựa trên tổng số frame
}

// Hàm dừng hoàn toàn animation để giải phóng CPU/RAM
const stopAnimation = () => {
    if (timer.value) {
        clearInterval(timer.value)
        timer.value = null
    }
}

let observer: IntersectionObserver | null = null

onMounted(() => {
    observer = new IntersectionObserver(([entry]) => {
        if (entry.isIntersecting) {
            isVisible.value = true
            isLoaded.value = true // Kích hoạt tải ảnh lần đầu
            startAnimation()      // Bật hiệu ứng ảnh động
        } else {
            isVisible.value = false
            stopAnimation()       // Tắt hiệu ứng khi cuộn mất dạng để nhẹ máy
        }
    }, {
        rootMargin: '80px' // Tải ảnh trước khi lọt vào tầm mắt 80px giúp cuộn mượt hơn
    })

    if (stickerRef.value) {
        observer.observe(stickerRef.value)
    }
})

onUnmounted(() => {
    stopAnimation()
    if (observer) {
        observer.disconnect()
    }
})

// Chuyển style sang Computed giúp Template cực kỳ sạch và tối ưu render
const stickerStyle = computed(() => {
    // Chỉ truyền URL khi cấu phần đã từng lọt vào vùng hiển thị (Lazy load thành công)
    const bgImage = isLoaded.value ? `url(${STICKER_URL + props.stickerItem.url})` : 'none'
    
    return {
        width: `${props.size}px`,
        height: `${props.size}px`,
        backgroundImage: bgImage,
        backgroundPosition: `${positionX.value}px 0px`,
        backgroundRepeat: 'repeat-x',
        backgroundSize: `${props.stickerItem.frameCount * props.size}px ${props.size}px`
    }
})
</script>