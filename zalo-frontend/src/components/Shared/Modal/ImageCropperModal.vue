<!-- components/ImageCropperModal.vue -->
<template>
  <Modal2 v-model="isOpen" :close-on-backdrop="false" @close="handleCancel" :title="title">
    <div class="flex flex-col items-center">
      <!-- Khung chứa ảnh để tương tác (Kéo thả di chuyển) -->
      <div 
        class="w-full h-80 bg-gray-900 overflow-hidden relative flex justify-center items-center cursor-move select-none rounded-lg"
        @mousedown="startDrag"
        @mousemove="onDrag"
        @mouseup="stopDrag"
        @mouseleave="stopDrag"
        @touchstart="startDrag"
        @touchmove="onDrag"
        @touchend="stopDrag"
      >
        <!-- Ảnh với Transform (Zoom & Pan) và ép kích thước base theo khung crop -->
        <img 
          ref="imageRef"
          v-if="imgSrc" 
          v-show="isLoaded"
          :src="imgSrc" 
          class="absolute max-none pointer-events-none"
          :style="{
            width: baseDisplayWidth + 'px',
            height: baseDisplayHeight + 'px',
            transform: `translate(${translateX}px, ${translateY}px) scale(${zoomValue})`,
            transformOrigin: 'center center'
          }"
          @load="onImageLoad"
          alt="Crop source"
        />

        <!-- Khung cắt (Crop Box) cố định ở giữa -->
        <div 
          ref="cropBoxRef"
          class="absolute border-2 border-white/80 shadow-[0_0_0_9999px_rgba(0,0,0,0.6)] pointer-events-none box-border flex items-center justify-center"
          :style="{
            width: boxWidth + 'px',
            height: boxHeight + 'px'
          }"
        >
          <!-- Đường lưới chia 9 ô -->
          <div class="w-full h-full border border-white/30 grid grid-cols-3 grid-rows-3 pointer-events-none">
            <div class="border-r border-b border-white/20"></div>
            <div class="border-r border-b border-white/20"></div>
            <div class="border-b border-white/20"></div>
            <div class="border-r border-b border-white/20"></div>
            <div class="border-r border-b border-white/20"></div>
            <div class="border-b border-white/20"></div>
            <div class="border-r border-white/20"></div>
            <div class="border-r border-white/20"></div>
            <div></div>
          </div>
        </div>
      </div>

      <!-- Thanh trượt Zoom -->
      <div class="flex items-center gap-3 w-full mt-4 px-4">
        <span class="text-sm text-gray-500">🔍</span>
        <input 
          type="range" 
          min="0.1" 
          max="3" 
          step="0.01" 
          v-model.number="zoomValue" 
          class="w-full accent-blue-600 cursor-pointer" 
        />
        <span class="text-sm text-gray-500">🔍</span>
      </div>

      <p class="text-xs text-gray-400 text-center mt-3">
        Dung lượng tối đa {{ maxSizeMB ?? 15 }}MB, kích thước tối thiểu {{ minWidth ?? 320 }}x{{ minHeight ?? 180 }} pixel.<br />
        Hỗ trợ định dạng JPG, PNG.
      </p>
    </div>

    <!-- Nút hành động -->
    <div class="flex gap-2 justify-end mt-4">
      <button type="button" @click="handleCancel"
        class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 font-medium transition">
        Hủy
      </button>
      <button type="button" @click="handleCrop"
        class="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium shadow transition">
        Đồng ý
      </button>
    </div>
  </Modal2>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import Modal2 from './Modal2.vue'

const props = defineProps<{
  modelValue: boolean
  imgSrc?: string | null
  title: string
  aspectRatio: number // Ví dụ: 16/9
  maxSizeMB?: number
  minWidth?: number
  minHeight?: number
}>()

const emit = defineEmits(['update:modelValue', 'cropped'])

const isOpen = ref(props.modelValue)
const imageRef = ref<HTMLImageElement | null>(null)
const isLoaded = ref(false)

const zoomValue = ref(1)
const translateX = ref(0)
const translateY = ref(0)

const isDragging = ref(false)
const startX = ref(0)
const startY = ref(0)

// Kích thước khung crop cố định trên UI
const boxWidth = 320
const boxHeight = computed(() => boxWidth / props.aspectRatio)

// Kích thước hiển thị cơ sở của ảnh để khớp hoàn toàn với khung
const baseDisplayWidth = ref(320)
const baseDisplayHeight = ref(180)

watch(() => props.modelValue, (val) => {
  isOpen.value = val
  if (val) {
    isLoaded.value = false
    zoomValue.value = 1
    translateX.value = 0
    translateY.value = 0
  }
})

// Khi ảnh load xong, tính toán kích thước hiển thị ban đầu sao cho phủ kín khung
const onImageLoad = () => {
  if (!imageRef.value) return
  const imgW = imageRef.value.naturalWidth
  const imgH = imageRef.value.naturalHeight

  // Fit ảnh theo khung crop
  const ratio = imgW / imgH
  if (ratio > props.aspectRatio) {
    baseDisplayHeight.value = boxHeight.value
    baseDisplayWidth.value = boxHeight.value * ratio
  } else {
    baseDisplayWidth.value = boxWidth
    baseDisplayHeight.value = boxWidth / ratio
  }
  
  const scaleX = boxWidth / baseDisplayWidth.value
  const scaleY = boxHeight.value / baseDisplayHeight.value
  zoomValue.value = Math.max(scaleX, scaleY, 1)

  translateX.value = 0
  translateY.value = 0
  isLoaded.value = true
}

const startDrag = (e: MouseEvent | TouchEvent) => {
  isDragging.value = true
  const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX
  const clientY = 'touches' in e ? e.touches[0].clientY : e.clientY
  startX.value = clientX - translateX.value
  startY.value = clientY - translateY.value
}

const onDrag = (e: MouseEvent | TouchEvent) => {
  if (!isDragging.value) return
  const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX
  const clientY = 'touches' in e ? e.touches[0].clientY : e.clientY
  translateX.value = clientX - startX.value
  translateY.value = clientY - startY.value
}

const stopDrag = () => {
  isDragging.value = false
}

// Thuật toán cắt ảnh khớp tuyệt đối 100% với ô ở giữa
const handleCrop = () => {
  if (!imageRef.value) return

  const img = imageRef.value
  const canvas = document.createElement('canvas')
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  // Độ phân giải ảnh đầu ra sắc nét (rộng 1200px)
  const outputWidth = 1200
  const outputHeight = outputWidth / props.aspectRatio
  canvas.width = outputWidth
  canvas.height = outputHeight

  ctx.clearRect(0, 0, outputWidth, outputHeight)

  // Kích thước thực tế của ảnh trên UI sau khi đã nhân zoom
  const currentRenderWidth = baseDisplayWidth.value * zoomValue.value
  const currentRenderHeight = baseDisplayHeight.value * zoomValue.value

  // Tỷ lệ chuyển đổi từ kích thước render sang kích thước ảnh gốc (natural)
  const ratioX = img.naturalWidth / currentRenderWidth
  const ratioY = img.naturalHeight / currentRenderHeight

  // Tính tâm vùng cắt dựa vào độ lệch dịch chuyển (translateX, translateY)
  const sourceCenterX = img.naturalWidth / 2 - (translateX.value * ratioX)
  const sourceCenterY = img.naturalHeight / 2 - (translateY.value * ratioY)

  // Kích thước vùng cần lấy trên ảnh gốc tương ứng với khung crop cố định
  const sourceWidth = boxWidth * ratioX
  const sourceHeight = boxHeight.value * ratioY

  // Tọa độ góc trên-trái của vùng cần cắt
  const sourceX = sourceCenterX - sourceWidth / 2
  const sourceY = sourceCenterY - sourceHeight / 2

  // Vẽ phần ảnh chuẩn xác vào canvas
  ctx.drawImage(
    img,
    sourceX, sourceY, sourceWidth, sourceHeight,
    0, 0, outputWidth, outputHeight
  )

  canvas.toBlob((blob) => {
    if (blob) {
      const croppedImageUrl = URL.createObjectURL(blob)
      emit('cropped', { blob, croppedImageUrl })
      closeModal()
    }
  }, 'image/jpeg', 0.9)
}

const handleCancel = () => {
  closeModal()
}

const closeModal = () => {
  isOpen.value = false
  emit('update:modelValue', false)
}
</script>