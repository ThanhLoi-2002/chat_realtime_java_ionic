<template>
    <div class="w-full text-sm rounded-lg space-y-4" :class="[style.bg.secondary, style.text.primary]">
        
        <div class="space-y-1.5">
            <div class="text-sm" :class="[style.text.primary]">Chọn khoảng thời gian</div>
            <div class="grid grid-cols-2 gap-2">
                <date-input v-model="localRange[0]" :placeholder="t('fromDate')"/>
                <date-input v-model="localRange[1]" :placeholder="t('toDate')"/>
            </div>
        </div>

        <div class="space-y-3 pt-1">
            <div class="flex items-center justify-between px-1">
                <span class="font-bold text-slate-100 text-[13px]">
                    {{ monthNames[currentMonth] }}, {{ currentYear }}
                </span>
                <div class="flex items-center gap-3">
                    <button type="button" @click="prevMonth" class="text-slate-400 hover:text-white cursor-pointer p-0.5">
                        <ion-icon :icon="chevronBackOutline" class="text-sm" />
                    </button>
                    <button type="button" @click="nextMonth" class="text-slate-400 hover:text-white cursor-pointer p-0.5">
                        <ion-icon :icon="chevronForwardOutline" class="text-sm" />
                    </button>
                </div>
            </div>

            <div class="grid grid-cols-7 text-center text-slate-400 font-medium text-[11px]">
                <div v-for="day in weekdays" :key="day" class="py-0.5">{{ day }}</div>
            </div>

            <div class="grid grid-cols-7 gap-y-1 text-center font-normal">
                <div v-for="(item, index) in daysInMonth" :key="index"
                     @click="handleDateClick(item.date)"
                     class="h-7 w-7 flex items-center justify-center text-xs cursor-pointer transition-all mx-auto relative"
                     :class="[getDateClass(item.date, item.isCurrentMonth)]">
                    {{ item.date.getDate() }}
                </div>
            </div>
        </div>

        <div class="flex items-center justify-end gap-2 pt-2 border-t border-slate-700/40">
            <button type="button" @click="emit('cancel')"
                class="px-4 py-1.5 rounded text-xs font-medium bg-slate-700/50 hover:bg-slate-700 text-slate-300 cursor-pointer">
                {{ t('cancel') }}
            </button>
            
            <button type="button" @click="handleConfirm"
                class="px-4 py-1.5 rounded text-xs font-medium bg-[#0068ff] hover:bg-blue-600 text-white cursor-pointer shadow-sm">
                {{ t('confirm') }}
            </button>
        </div>

    </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { style } from '@/assets/tailwindcss'
import { calendarOutline, chevronBackOutline, chevronForwardOutline } from 'ionicons/icons'
import { useTranslate } from '@/composables/useTranslate'
import DateInput from './DateInput.vue'

const { t } = useTranslate()

// Thay vì defineModel, nhận giá trị cũ làm Prop đầu vào
const props = defineProps<{
    value: [Date | null, Date | null] | undefined
}>()

const emit = defineEmits<{
    (e: 'cancel'): void
    (e: 'confirm', dateRange: [Date | null, Date | null]): void
}>()

// Biến tạm để lưu dở dang quá trình chọn ngày của User
const localRange = ref<[Date | null, Date | null]>([null, null])

// Đồng bộ ngược lại local mỗi khi mở Popover lên để hiển thị đúng ngày cũ đang chọn
watch(() => props.value, (newVal) => {
    if (newVal) {
        localRange.value = [...newVal]
    }
}, { immediate: true, deep: true })

const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth())

const weekdays = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']
const monthNames = ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12']

// --- KHI BẤM XÁC NHẬN ---
const handleConfirm = () => {
    // Xử lý tối ưu: nếu có ngày kết thúc, thiết lập thời gian về cuối ngày 23:59:59 tránh sót tin nhắn
    const [start, end] = localRange.value
    if (start && end) {
        end.setHours(23, 59, 59, 999)
    }
    emit('confirm', [start, end])
}

// --- LOGIC TẠO LƯỚI LỊCH ---
const daysInMonth = computed(() => {
    const cells = []
    const firstDayIndex = new Date(currentYear.value, currentMonth.value, 1).getDay()
    const totalDays = new Date(currentYear.value, currentMonth.value + 1, 0).getDate()
    const prevTotalDays = new Date(currentYear.value, currentMonth.value, 0).getDate()

    for (let i = firstDayIndex - 1; i >= 0; i--) {
        cells.push({ date: new Date(currentYear.value, currentMonth.value - 1, prevTotalDays - i), isCurrentMonth: false })
    }
    for (let i = 1; i <= totalDays; i++) {
        cells.push({ date: new Date(currentYear.value, currentMonth.value, i), isCurrentMonth: true })
    }
    const remainingCells = 42 - cells.length
    for (let i = 1; i <= remainingCells; i++) {
        cells.push({ date: new Date(currentYear.value, currentMonth.value + 1, i), isCurrentMonth: false })
    }
    return cells
})

// --- LOGIC CLICK NGÀY (Cập nhật trực tiếp vào localRange) ---
const handleDateClick = (date: Date) => {
    const clickedDate = new Date(date)
    clickedDate.setHours(0, 0, 0, 0)

    const [start, end] = localRange.value

    if (!start || (start && end)) {
        localRange.value = [clickedDate, null]
    } else if (start && !end) {
        const startTime = new Date(start).setHours(0, 0, 0, 0)
        if (clickedDate.getTime() < startTime) {
            localRange.value = [clickedDate, null]
        } else {
            localRange.value = [start, clickedDate]
        }
    }
}

// --- CSS HIGHLIGHTS CHUẨN (Tính theo localRange) ---
const getDateClass = (date: Date, isCurrentMonth: boolean) => {
    const dateTime = new Date(date).setHours(0, 0, 0, 0)
    const [start, end] = localRange.value
    
    const startTime = start ? new Date(start).setHours(0, 0, 0, 0) : null
    const endTime = end ? new Date(end).setHours(0, 0, 0, 0) : null

    if (startTime && dateTime === startTime) return 'bg-[#0068ff] text-white rounded-md z-10'
    if (endTime && dateTime === endTime) return 'bg-[#0068ff] text-white rounded-md z-10'

    if (startTime && endTime && dateTime > startTime && dateTime < endTime) {
        return 'bg-[#0068ff]/15 text-blue-400 font-medium'
    }

    return isCurrentMonth ? 'text-slate-200 hover:bg-slate-700/60 rounded-md' : 'text-slate-600'
}

const prevMonth = () => {
    if (currentMonth.value === 0) { currentMonth.value = 11; currentYear.value-- }
    else { currentMonth.value-- }
}
const nextMonth = () => {
    if (currentMonth.value === 11) { currentMonth.value = 0; currentYear.value++ }
    else { currentMonth.value++ }
}
</script>