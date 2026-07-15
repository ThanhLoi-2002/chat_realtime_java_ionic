<template>
    <div class="flex justify-between items-center">
        <label class="dark:text-white w-1/3">{{ t(label) }}</label>
        <div class="w-full">
            <input v-model="value" v-bind="attrs"
                :class="[oaStyle.bg.primary, oaStyle.border.primary, oaStyle.text.primary, 'w-full px-3 py-2 rounded-md border']" />
            <span v-if="errors[name]" class="text-red-500 text-sm mt-1">{{ t(errors[name]) }}</span>
        </div>
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import { useTranslate } from '@/composables/useTranslate';

const props = defineProps<{
    label: string
    defineField: any
    name: string
    errors: any
}>()

const [value, attrs] = props.defineField(props.name)
const { t } = useTranslate()
</script>