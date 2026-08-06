<template>
    <div class="flex items-center gap-3">
        <!-- Click trực tiếp vào khung tròn ảnh đại diện để mở input file -->
        <div @click="imageRef?.click()"
            :class="[oaStyle.bg.secondary, oaStyle.border.secondary, size ?? 'size-12', 'rounded-full overflow-hidden shrink-0 border flex items-center justify-center cursor-pointer hover:opacity-90 transition group relative']">
            <img v-if="avatar" :src="handlePickerUrl(avatar)" class="w-full h-full object-center" />
            <i v-else class="fas fa-camera text-gray-400 text-xs"></i>

            <!-- Hiệu ứng lớp phủ icon máy ảnh khi hover vào ảnh -->
            <div
                class="absolute inset-0 bg-black/30 opacity-0 group-hover:opacity-100 transition flex items-center justify-center rounded-full">
                <i class="fas fa-camera text-white text-[10px]"></i>
            </div>
        </div>

        <!-- Input file giữ nguyên -->
        <input ref="imageRef" type="file" accept="image/*" class="hidden" @change="onChangeAvatar" />
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import { useImagePicker } from '@/composables/useImagePicker'
import { ref } from 'vue'

defineProps<{
    avatar?: string
    size?: string
}>()

const emit = defineEmits<{
    (e: 'update:avatar', file: File): void
    (e: 'preview', url: string): void
}>()

const imageRef = ref<HTMLInputElement>()

const { handlePickerUrl } = useImagePicker()

const onChangeAvatar = (event: Event) => {
    const target = event.target as HTMLInputElement

    if (!target.files?.length) return

    const file = target.files[0]
    const preview = URL.createObjectURL(file)

    emit('preview', preview)        // cập nhật ảnh hiển thị
    emit('update:avatar', file)     // gửi File để upload
}
</script>