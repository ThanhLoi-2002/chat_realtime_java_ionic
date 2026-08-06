<template>
    <div class="flex items-center gap-3">
        <!-- Click vào khung ảnh để mở input file -->
        <div @click="imageRef?.click()"
            :class="[oaStyle.bg.secondary, oaStyle.border.secondary, size ?? 'size-16', 'size-16 rounded overflow-hidden shrink-0 border flex items-center justify-center cursor-pointer hover:opacity-90 transition group relative']">
            <img v-if="cover" :src="handlePickerUrl(cover)" class="w-full h-full object-center" />
            <i v-else class="fas fa-image text-gray-400 text-xs"></i>

            <!-- (Tùy chọn) Hiệu ứng nhỏ khi hover vào ảnh để người dùng biết là bấm được -->
            <div
                class="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition flex items-center justify-center">
                <i class="fas fa-camera text-white text-xs"></i>
            </div>
        </div>

        <!-- Input file ẩn giữ nguyên -->
        <input ref="imageRef" type="file" accept="image/*" class="hidden" @change="onFileSelected" />

        <!-- Gọi Modal Cắt ảnh -->
        <ImageCropperModal v-model="showCropper" :imgSrc="cover" :aspect-ratio="16 / 9" title="changeCover"
            @cropped="onCropperResult" />
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import { useImagePicker } from '@/composables/useImagePicker';
import { toast } from '@/utils/toast';
import { ref } from 'vue';

defineProps<{
    cover?: string
    size?: string
}>()

const { handlePickerUrl } = useImagePicker()
const emit = defineEmits<{
    (e: 'update:cover', file: File): void
    (e: 'preview', url: string): void
}>()

const showCropper = ref(false)
const imageRef = ref<HTMLInputElement>()

const onFileSelected = (event: Event) => {
    const target = event.target as HTMLInputElement

    if (target.files && target.files[0]) {
        const file = target.files[0]
        // Kiểm tra dung lượng (Ví dụ tối đa 15MB)
        if (file.size > 15 * 1024 * 1024) {
            toast({ message: 'Dung lượng ảnh vượt quá 15MB!', color: 'danger' })
            return
        }

        const preview = URL.createObjectURL(file)

        // Đọc file thành đường dẫn tạm thời để đưa vào Cropper
        showCropper.value = true // Mở modal cắt ảnh
        emit('preview', preview)        // cập nhật ảnh hiển thị
        emit('update:cover', file)     // gửi File để upload

        // Reset input để có thể chọn lại chính file đó lần sau nếu muốn
        target.value = ''
    }
}

// Nhận kết quả trả về từ Modal cắt ảnh
const onCropperResult = ({ blob, croppedImageUrl }: any) => {
    emit('preview', croppedImageUrl)        // cập nhật ảnh hiển thị
    emit('update:cover', blob)     // gửi File để upload
}
</script>