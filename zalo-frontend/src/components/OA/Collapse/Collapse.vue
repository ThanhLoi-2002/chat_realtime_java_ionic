<template>
    <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'border rounded-lg shadow-sm mb-4']">
        <div @click="emit('update:isOpen', !isOpen)"
            :class="[isOpen && `${oaStyle.border.secondary} border-b`, 'px-6 py-4 flex justify-between items-center cursor-pointer']">
            <div :class="[oaStyle.text.primary, 'text-md font-medium']">{{ t(title) }}</div>
            <i
                :class="[oaStyle.text.secondary, isOpen ? 'fas fa-chevron-up text-xs' : 'fas fa-chevron-down text-xs']"></i>
        </div>

        <transition name="collapse">
            <div v-show="isOpen" :class="customClass">
                <slot />
            </div>
        </transition>
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import { useTranslate } from '@/composables/useTranslate';

const props = defineProps<{
    isOpen: boolean
    title: string
    customClass?: string
}>()

const emit = defineEmits(['update:isOpen'])
const { t } = useTranslate()

</script>
<style>
.collapse-enter-active,
.collapse-leave-active {
    transition: all 0.5s ease;
}

.collapse-enter-from,
.collapse-leave-to {
    opacity: 0;
    transform: translateY(-8px);
    max-height: 0;
}

.collapse-enter-to,
.collapse-leave-from {
    opacity: 1;
    transform: translateY(0);
    max-height: 500px;
    /* đủ lớn để chứa content */
}
</style>