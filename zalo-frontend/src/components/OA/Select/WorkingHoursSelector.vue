<template>
    <!-- Khu vực chọn thời gian -->
    <div class="flex-col w-full space-y-3">
        <div class="grid grid-cols-2 gap-3">
            <!-- Thời gian bắt đầu -->
            <div class="w-full">
                <label :class="[oaStyle.text.secondary, 'block text-sm mb-1']">{{ t('startHour') }}</label>
                <input type="time" v-model="startHour" :disabled="isWholeDay" :class="[
                    oaStyle.bg.secondary,
                    `${oaStyle.border.secondary} focus:ring-blue-400 focus:ring-1`,
                    oaStyle.text.secondary,
                    isWholeDay ? 'opacity-50 cursor-not-allowed' : '',
                    'w-full px-3 py-2 rounded-md border focus:outline-none'
                ]" />
            </div>

            <!-- Thời gian kết thúc -->
            <div class="w-full">
                <label :class="[oaStyle.text.secondary, 'block text-sm mb-1']">{{ t('endHour') }}</label>
                <input type="time" v-model="endHour" :disabled="isWholeDay" :class="[
                    oaStyle.bg.secondary,
                    `${oaStyle.border.secondary} focus:ring-blue-400 focus:ring-1`,
                    oaStyle.text.secondary,
                    isWholeDay ? 'opacity-50 cursor-not-allowed' : '',
                    'w-full px-3 py-2 rounded-md border focus:outline-none'
                ]" />
            </div>
        </div>

        <!-- Checkbox 24/24 -->
        <div class="flex items-center space-x-2 mt-2">
            <input type="checkbox" id="wholeDay" v-model="isWholeDay"
                :class="[oaStyle.border.secondary, 'w-4 h-4 text-blue-600 rounded focus:ring-blue-500 cursor-pointer']" />
            <label for="wholeDay" :class="[oaStyle.text.secondary, 'text-sm cursor-pointer select-none']">
                24/24
            </label>
        </div>
    </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss'
import { useTranslate } from '@/composables/useTranslate';

// const props = defineProps<{
//     label: string
// }>()

const { t } = useTranslate()

// Sử dụng defineModel để đồng bộ 2 chiều với form cha (nếu form cha dùng v-model:working-hours)
// Hoặc bạn có thể đổi tên model tùy theo cấu trúc dữ liệu của bạn trong OaType
const startHour = defineModel<string>('startHour', { default: '08:00' })
const endHour = defineModel<string>('endHour', { default: '22:00' })
const isWholeDay = defineModel<boolean>('isWholeDay', { default: false })
</script>