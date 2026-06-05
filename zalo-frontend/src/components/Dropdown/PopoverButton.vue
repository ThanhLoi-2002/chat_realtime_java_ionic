<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import { RoundedStyle } from '@/types/style';
import { computed } from 'vue';

const props = defineProps<{
    id: string
    label: string
    rounded: RoundedStyle
}>()

const model = defineModel<any>()
const open = defineModel<boolean>('open', { default: false })

const resetFilter = () => {
    model.value = undefined
}

const hasValue = computed(() => {
    return Array.isArray(model.value)
        ? !!model.value[0] && !!model.value[1]
        : !!model.value
})
</script>

<template>
    <button :id="id" type="button" class="inline-flex items-center justify-between h-7 w-full pl-2.5 pr-1.5 text-xs font-normal border transition-colors focus:outline-none
             dark:text-slate-200 cursor-pointer" :class="[
                rounded,
                // NẾU CÓ MODEL: Đổi sang màu xanh (cả light mode và dark mode)
                hasValue
                    ? 'bg-blue-100 border-blue-100 hover:bg-blue-200 text-blue-700 dark:bg-blue-600 dark:hover:bg-blue-600/80 dark:text-blue-300 dark:border-blue-600'
                    : `${style.bg.secondary} ${style.bg.hover} ${style.text.secondary} ${style.border.primary}`
            ]" @click="open = true">
        <span class="truncate max-w-24">{{ label }}</span>

        <div class="ml-1 flex items-center justify-center w-4 h-4">
            <i v-if="hasValue"
                class="fas fa-times-circle text-slate-400 hover:text-slate-600 dark:text-slate-400 dark:hover:text-slate-200 text-sm cursor-pointer"
                @click.stop="resetFilter"></i>

            <i v-else class="fas fa-angle-down text-sm text-slate-500 dark:text-slate-400 pointer-events-none"></i>
        </div>
    </button>

    <ion-popover :trigger="id" trigger-action="click" :is-open="open" :show-backdrop="false" @didDismiss="open = false"
        side="bottom" alignment="start" class="mt-2">
        <slot />
    </ion-popover>
</template>