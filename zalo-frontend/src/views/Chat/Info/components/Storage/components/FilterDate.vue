<script setup lang="ts">
import { ref } from 'vue'
import { style } from '@/assets/tailwindcss'
import { useTranslate } from '@/composables/useTranslate'
import DateRangePicker from '@/components/Shared/DatePicker/DateRangePicker.vue'

const { t } = useTranslate()
const model = defineModel<[Date | null, Date | null]>() // Định dạng: [Date, Date] hoặc null
const open = ref(false)
const showPresets = ref(false)

// Format hiển thị text trên Button chính ngoài bộ lọc
const getSelectedLabel = () => {
    if (model.value && model.value[0] && model.value[1]) {
        const format = (d: Date) => `${d.getDate().toString().padStart(2, '0')}/${(d.getMonth() + 1).toString().padStart(2, '0')}`
        return `${format(model.value[0])} - ${format(model.value[1])}`
    }
    return t('sendDate')
}

// Hàm chọn khoảng ngày nhanh
const selectPreset = (days: number) => {
    const end = new Date()
    const start = new Date()
    if (days === 90) {
        start.setMonth(start.getMonth() - 3)
    } else {
        start.setTime(start.getTime() - days * 86400000)
    }
    model.value = [start, end]
    showPresets.value = false
    open.value = false
}

const cancel = () => {
    open.value = false
}

// Khi bấm xác nhận ở Calendar con, nó sẽ bắn dữ liệu thông qua hàm này
const confirm = (dateRange: [Date | null, Date | null]) => {
    model.value = dateRange // Update giá trị thật vào filter cha
    open.value = false     // Đóng Popover
}
</script>

<template>
    <popover-button id="sent-date-popover" :label="getSelectedLabel()" v-model:open="open" v-model="model"
        rounded="rounded-2xl">
        <div class="w-full text-slate-200 select-none text-[13px] relative rounded-lg shadow-xl border border-slate-700/60 p-3 space-y-3"
            :class="[style.bg.secondary]">

            <div class="relative" @mouseenter="showPresets = true" @mouseleave="showPresets = false">

                <div class="flex items-center justify-between py-1 px-2 rounded cursor-pointer transition-colors"
                    :class="[style.text.primary, style.bg.hover]">
                    <span>Gợi ý thời gian</span>
                    <i class="fas fa-angle-right text-xs"></i>
                </div>

                <div v-if="showPresets"
                    class="absolute left-0 top-0 w-full border border-slate-700/60 rounded-lg p-1 shadow-2xl space-y-0.5 z-50 animate-fade-in"
                    :class="[style.text.primary, style.bg.secondary, style.text.secondary]">
                    <div @click="selectPreset(7)" class="p-2 rounded cursor-pointer text-[13px]" :class="[style.bg.hover]">7
                        ngày trước
                    </div>
                    <div @click="selectPreset(30)" class="p-2 rounded cursor-pointer text-[13px]" :class="[style.bg.hover]">
                        30 ngày trước
                    </div>
                    <div @click="selectPreset(90)" class="p-2 rounded cursor-pointer text-[13px]" :class="[style.bg.hover]">
                        3 tháng trước
                    </div>
                </div>
            </div>

            <hr class="border-slate-700/40" />

            <DateRangePicker :value="model" @cancel="cancel" @confirm="confirm"/>
        </div>
    </popover-button>
</template>