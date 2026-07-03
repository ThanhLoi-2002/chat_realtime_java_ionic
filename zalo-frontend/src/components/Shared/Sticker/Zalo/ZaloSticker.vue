<template>
    <div ref="stickerRef" 
         class="overflow-hidden cursor-pointer rounded-lg" 
         :class="[isHover && 'dark:hover:bg-slate-700']"
         :style="stickerStyle"
         @click="emit('select', props.stickerItem)"
         @mouseenter="handleMouseEnter"
         @mouseleave="handleMouseLeave" 
         />
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { StickerItemType } from "@/types/entities"
import { STICKER_URL } from '@/utils/constant'
import { onLongPress } from '@vueuse/core';

const props = defineProps<{
    stickerItem: StickerItemType
    size: number
    isHover: boolean
    isUseDefaultUrl?: boolean
}>()

const emit = defineEmits<{
    select: [StickerItemType]
    preview: [StickerItemType]
}>()

// State quản lý DOM và Lazy Load
const stickerRef = ref<HTMLDivElement | null>(null)
const isVisible = ref(false)
const isLoaded = ref(false)

// State quản lý Animation và Số vòng lặp
const currentFrame = ref(0)
const positionX = ref(0)
const timer = ref<ReturnType<typeof setInterval> | null>(null)
const loopCount = ref(0) // Bộ đếm số lần lặp (chạy hết tất cả các frame là 1 lần)
const MAX_LOOP = props.stickerItem.frameCount >= 10 ? 3 : 6;

// Hàm khởi động animation
const startAnimation = () => {
    if (timer.value) return
    
    // Đảm bảo an toàn nếu dữ liệu frameCount lỗi
    const totalFrames = props.stickerItem.frameCount || 1;

    timer.value = setInterval(() => {
        // Tính toán frame tiếp theo
        const nextFrame = currentFrame.value + 1;
        
        // KIỂM TRA: Nếu chuẩn bị nhảy sang vòng lặp mới
        if (nextFrame >= totalFrames) {
            loopCount.value++; // Tăng số vòng đã chạy lên 1
            
            // Nếu đã chạy đủ MAX_LOOP lần hoàn chỉnh, dừng ngay lập tức
            if (loopCount.value >= MAX_LOOP) {
                currentFrame.value = 0; // Đưa về frame đầu tiên
                positionX.value = 0;
                stopAnimation();
                return;
            }
        }

        // Cập nhật vị trí frame như bình thường
        currentFrame.value = nextFrame % totalFrames;
        positionX.value = -(currentFrame.value * props.size);
    }, 100) // Đổi tốc độ cố định 100ms/frame để chuyển động tự nhiên nhất
}

// Hàm dừng hoàn toàn animation
const stopAnimation = () => {
    if (timer.value) {
        clearInterval(timer.value)
        timer.value = null
    }
}

// --- LOGIC XỬ LÝ SỰ KIỆN HOVER ---
const handleMouseEnter = () => {
    // Chỉ chạy khi ảnh đã load xong và đang nằm trong màn hình
    if (!isLoaded.value) return
    
    loopCount.value = 0;   // Reset lại bộ đếm về 0 để chuẩn bị chạy 3 lần mới
    currentFrame.value = 0; // Chạy từ frame đầu tiên
    startAnimation();       // Kích hoạt lại animation
}

const handleMouseLeave = () => {
    // Tùy chọn: Nếu muốn người dùng rời chuột ra là dừng luôn (không cần đợi đủ 3 lần)
    // Bạn có thể mở comment dòng dưới. Nếu muốn rời chuột ra vẫn chạy nốt cho đủ 3 lần thì giữ nguyên.
    // stopAnimation();
}

// Quản lý lazy-load hiển thị màn hình
let observer: IntersectionObserver | null = null

onMounted(() => {
    observer = new IntersectionObserver(([entry]) => {
        if (entry.isIntersecting) {
            isVisible.value = true
            isLoaded.value = true
            
            // Khi vừa cuộn vào màn hình: Reset đếm và chạy 3 lần đầu rồi tự tắt
            loopCount.value = 0
            startAnimation()
        } else {
            isVisible.value = false
            stopAnimation()
        }
    }, {
        rootMargin: '80px'
    })

    if (stickerRef.value) {
        observer.observe(stickerRef.value)
    }
})

watch(() => props.stickerItem.url, () => startAnimation())

onUnmounted(() => {
    stopAnimation()
    if (observer) {
        observer.disconnect()
    }
})

const stickerStyle = computed(() => {
    const url = props.isUseDefaultUrl ? props.stickerItem.url : STICKER_URL + props.stickerItem.url
    const bgImage = isLoaded.value ? `url(${url})` : 'none'
    
    return {
        width: `${props.size}px`,
        height: `${props.size}px`,
        backgroundImage: bgImage,
        backgroundPosition: `${positionX.value}px 0px`,
        backgroundRepeat: 'no-repeat',
        backgroundSize: `${props.stickerItem.frameCount * props.size}px ${props.size}px`
    }
})

// Cấu hình VueUse cho sự kiện nhấn giữ
onLongPress(
  stickerRef,
  () => emit('preview', props.stickerItem),
  {
    delay: 500, // Thời gian tính bằng mili-giây (ví dụ: 800ms)
    modifiers: {
      prevent: true, // Ngăn chặn hành vi mặc định (ví dụ: bôi đen chữ)
      stop: true     // Ngăn chặn sự kiện lan truyền (bubbling) lên cha
    }
  }
);
</script>