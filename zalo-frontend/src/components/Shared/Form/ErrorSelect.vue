<template>
    <div :class="[
        'flex gap-2',
        direction === 'vertical' ? 'flex-col' : 'justify-between items-start'
    ]">
        <label :class="[oaStyle.text.primary, direction === 'vertical' ? 'w-full' : 'w-1/3']">
            {{ t(label) }}
            <span v-if="required" class="text-red-500">
                *
            </span>
        </label>

        <div class="w-full">
            <select :id="name" :name="name" v-model="value" v-bind="attrs" :class="[
                oaStyle.bg.primary,
                errors[name] ? 'border-red-500' : `${oaStyle.border.primary} focus:ring-blue-400 focus:ring-1`,
                oaStyle.text.primary,
                'w-full px-3 py-2 rounded-md border focus:outline-none'
            ]">
                <option v-if="placeholder" disabled value="">
                    {{ t(placeholder) }}
                </option>

                <option v-for="item in options" :key="item.value" :value="item.value">
                    {{ t(item.label) }}
                </option>
            </select>

            <span v-if="errors[name]" class="text-red-500 text-sm mt-1 block">
                {{ t(errors[name]) }}
            </span>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { oaStyle } from "@/assets/tailwindcss";
import { useTranslate } from "@/composables/useTranslate";
import { SelectOptionType } from "@/types/common";

const props = defineProps<{
    label: string
    defineField: any
    name: string
    errors: any
    schema: any
    options: SelectOptionType[]
    placeholder?: string
    direction: 'horizontal' | 'vertical'
}>()

const [value, attrs] = props.defineField(props.name)
const { t } = useTranslate()

const required = computed(() => {
    const desc = props.schema?.describe?.()
    return desc?.fields?.[props.name]
        ? !desc.fields[props.name].optional
        : false
})
</script>