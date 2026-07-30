<template>
    <div :class="[
        'flex gap-2',
        direction === 'vertical' ? 'flex-col' : 'justify-between items-start'
    ]">
        <!-- Label -->
        <label :class="[oaStyle.text.primary, direction === 'vertical' ? 'w-full' : 'w-1/3']">
            {{ t(label) }}
            <span v-if="required" class="text-red-500">*</span>
        </label>

        <!-- Input / Textarea Container -->
        <div class="w-full">
            <input v-if="!isTextarea" v-model="value" v-bind="attrs" :name="name" :placeholder="t(placeholder ?? '')"
                :class="[
                    oaStyle.bg.primary,
                    oaStyle.text.primary,
                    errors[name] ? 'border-red-500' : `${oaStyle.border.primary} focus:ring-blue-400 focus:ring-1`,
                    'w-full px-3 py-2 rounded-md border focus:outline-none'
                ]" />

            <!-- Textarea -->
            <textarea v-else :id="name" :name="name" v-model="value" v-bind="attrs" rows="4" :placeholder="placeholder"
                :class="[
                    oaStyle.bg.primary,
                    errors[name] ? 'border-red-500' : `${oaStyle.border.primary} focus:ring-blue-400 focus:ring-1`,
                    oaStyle.text.primary,
                    'w-full px-3 py-2 rounded-md border focus:outline-none'
                ]" />

            <span v-if="errors[name]" class="text-red-500 text-sm mt-1 block">{{ t(errors[name]) }}</span>
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
    schema: any
    isTextarea?: boolean
    placeholder?: string
    direction: 'horizontal' | 'vertical'
}>()

const [value, attrs] = props.defineField(props.name)
const { t } = useTranslate()
const desc = props.schema?.describe()

const required = !desc.fields[props.name].optional
</script>