<!-- components/BaseModal.vue -->
<template>
    <Transition name="modal-fade">
        <div
            v-if="modelValue"
            class="fixed inset-0 z-50 flex items-center justify-center"
        >
            <!-- Backdrop -->
            <div
                class="fixed inset-0 bg-black/50 transition-opacity"
                @click="closeOnBackdrop && close()"
            ></div>

            <!-- Modal -->
            <div
                :class="[
                    style.bg.secondary,
                    maxW ?? 'max-w-xl',
                    'relative z-10 mx-6 flex max-h-[90vh] w-full flex-col rounded-lg p-6 shadow-xl'
                ]"
            >
                <!-- Header: luôn hiển thị -->
                <div
                    :class="[
                        style.border.secondary,
                        'flex shrink-0 items-center justify-between border-b pb-3'
                    ]"
                >
                    <div :class="[style.text.primary, 'text-lg font-medium']">
                        {{ t(title) }}
                    </div>

                    <button
                        @click="close"
                        :class="[style.text.primary, 'focus:outline-none']"
                    >
                        <i class="fas fa-close"></i>
                    </button>
                </div>

                <!-- Body: có thanh cuộn riêng -->
                <div class="min-h-0 flex-1 overflow-y-auto py-4 pr-3">
                    <slot></slot>
                </div>
            </div>
        </div>
    </Transition>
</template>

<script setup lang="ts">
import { style } from '@/assets/tailwindcss'
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