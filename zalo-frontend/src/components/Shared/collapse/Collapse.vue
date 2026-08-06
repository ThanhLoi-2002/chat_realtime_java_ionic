<template>
    <section :class="[oaStyle.border.secondary, 'border-b']">
        <div class="px-4 py-2 flex justify-between items-center cursor-pointer" @click="emit('update:isOpen', !isOpen)">
            <span :class="[oaStyle.text.primary, 'font-bold']">{{ title }}</span>
            <span :class="[isOpen ? 'rotate-180' : '', style.text.primary]" class="transition">
                <i class="fas fa-sort-down"></i>
            </span>
        </div>

        <transition name="collapse">
            <div v-show="isOpen" class="flex flex-col mb-2 overflow-hidden" :class="customClass">
                <slot />
            </div>
        </transition>
    </section>
</template>
<script setup lang="ts">
import { oaStyle, style } from '@/assets/tailwindcss';

const props = defineProps<{
    isOpen: boolean
    title: string
    customClass?: string
}>()

const emit = defineEmits(['update:isOpen'])

</script>