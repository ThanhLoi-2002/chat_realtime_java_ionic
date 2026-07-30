<!-- components/BaseModal.vue -->
<template>
    <Transition name="modal-fade">
        <div v-if="modelValue" class="fixed inset-0 z-50 flex items-center justify-center">
            <!-- Backdrop (Lớp nền mờ tối): Bấm vào đây để đóng modal -->
            <div :class="[oaStyle.bg.primary, 'fixed inset-0 transition-opacity']" @click="closeOnBackdrop && close()">
            </div>

            <!-- Khung chứa nội dung Modal -->
            <div :class="[oaStyle.bg.secondary, maxW ?? 'max-w-xl', 'relative z-10 w-full rounded-lg p-6 mx-6 shadow-xl']">
                <!-- Header -->
                <div class="flex items-center justify-between pb-3 border-b">
                    <div :class="[oaStyle.text.primary, 'text-lg font-medium']">
                        {{ t(title) }}
                    </div>
                    <button @click="close" :class="[oaStyle.text.primary, 'focus:outline-none']">
                        <i class="fas fa-close"/>
                    </button>
                </div>

                <!-- Body (Nội dung động truyền vào) -->
                <div class="py-4">
                    <slot></slot>
                </div>
            </div>
        </div>
    </Transition>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss'
import { useTranslate } from '@/composables/useTranslate';
import { onMounted, onUnmounted } from 'vue'

const props = defineProps<{
    modelValue: boolean
    closeOnBackdrop: boolean
    title: string
    maxW?: string
}>()

const { t } = useTranslate()
const emit = defineEmits(['update:modelValue', 'close'])

const close = () => {
    emit('update:modelValue', false)
    emit('close')
}

// (Tùy chọn) Chặn cuộn trang chính khi modal đang mở
onMounted(() => {
    document.body.style.overflow = 'hidden'
})
onUnmounted(() => {
    document.body.style.overflow = 'auto'
})
</script>

<style scoped>
/* Hiệu ứng chuyển động Fade In / Fade Out mượt mà */
.modal-fade-enter-active,
.modal-fade-leave-active {
    transition: opacity 0.25s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
    opacity: 0;
}
</style>