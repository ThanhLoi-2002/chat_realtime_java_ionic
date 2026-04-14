<template>
    <section class="p-4 border-b border-gray-200 dark:border-slate-700">
        <div class="flex justify-between items-center mb-3 cursor-pointer" @click="emit('update:isOpen', !isOpen)">
            <span class="font-medium dark:text-white">{{ title }}</span>
            <span :class="[isOpen ? 'rotate-180' : '', style.text.primary]" class="transition">
                ▼
            </span>
        </div>

        <transition name="collapse">
            <div v-show="isOpen" class="flex flex-col gap-4 overflow-hidden" :class="customClass">
                <slot />
            </div>
        </transition>
    </section>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';

const props = defineProps<{
    isOpen: boolean
    title: string
    customClass?: string
}>()

const emit = defineEmits(['update:isOpen'])

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