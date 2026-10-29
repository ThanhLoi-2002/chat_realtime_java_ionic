<!-- components/ErrorGiftInput.vue -->
<script setup lang="ts">
import { GiftDTO } from '@/types/entities';
import { computed } from 'vue'

const props = defineProps<{
    name: string
    label?: string
    errors: Record<string, any>
    defineField: (field: any) => [any, Record<string, any>]
    direction?: 'vertical' | 'horizontal'
}>()

const [gifts] = props.defineField(props.name)

const giftList = computed<GiftDTO[]>({
    get() {
        if (Array.isArray(gifts.value) && gifts.value.length > 0) {
            return gifts.value
        }
        return []
    },
    set(val) {
        gifts.value = val
    }
})

const updateRow = (index: number, key: keyof GiftDTO, value: string) => {
    const newList = [...giftList.value]
    newList[index] = { ...newList[index], [key]: value }
    giftList.value = newList
}

const addRow = () => {
    giftList.value = [...giftList.value, { title: '', note: '', icon: 'fa-gift' }]
}

const removeRow = (index: number) => {
    giftList.value = giftList.value.filter((_, i) => i !== index)
}
</script>

<template>
    <div :class="['flex', direction === 'vertical' ? 'flex-col gap-1.5' : 'flex-row items-center gap-4']">
        <div class="flex items-center justify-between">
            <label v-if="label" class="text-sm font-semibold capitalize text-slate-300">
                {{ label }}
            </label>
            <button type="button" @click="addRow"
                class="text-xs font-semibold text-blue-400 hover:text-blue-300 flex items-center gap-1 cursor-pointer">
                <i class="fas fa-plus"></i> Thêm quà tặng
            </button>
        </div>

        <div class="space-y-3 max-h-64 overflow-y-auto pr-1">
            <div v-for="(item, index) in giftList" :key="index"
                class="p-2.5 bg-slate-900 border border-slate-700 rounded-lg space-y-2 relative group">
                <div class="flex items-center gap-2">
                    <!-- Tên quà tặng -->
                    <input type="text" :value="item.title"
                        @input="(e) => updateRow(index, 'title', (e.target as HTMLInputElement).value)"
                        placeholder="Tên quà tặng (VD: Tặng zStyle...)..."
                        class="flex-1 bg-slate-800 border border-slate-600 rounded px-2.5 py-1 text-sm text-slate-100 focus:outline-none focus:border-blue-500" />

                    <!-- Nút Xóa -->
                    <button type="button" @click="removeRow(index)"
                        class="text-slate-400 hover:text-red-400 px-1 text-sm transition-colors cursor-pointer"
                        title="Xóa quà này">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </div>

                <!-- Ô nhập Icon (VD: fa-gift, fa-crown) -->
                <input type="text" :value="item.icon"
                    @input="(e) => updateRow(index, 'icon', (e.target as HTMLInputElement).value)"
                    placeholder="Icon (fa-gift)..."
                    class="w-full bg-slate-800 border border-slate-600 rounded px-2 py-1 text-xs text-blue-400 focus:outline-none focus:border-blue-500"
                    title="Tên icon FontAwesome (VD: fa-gift, fa-star)" />

                <!-- Ghi chú / Điều kiện nhận quà -->
                <input type="text" :value="item.note"
                    @input="(e) => updateRow(index, 'note', (e.target as HTMLInputElement).value)"
                    placeholder="Ghi chú/Điều kiện (VD: khi nâng cấp gói Pro 12 tháng)..."
                    class="w-full bg-slate-800/50 border border-slate-700 rounded px-2.5 py-1 text-xs text-slate-400 italic focus:outline-none focus:border-blue-500" />
            </div>

            <div v-if="giftList.length === 0" class="text-xs text-slate-500 italic text-center py-2">
                Chưa có quà tặng nào cho gói này.
            </div>
        </div>

        <span v-if="errors && errors[name]" class="text-xs text-red-500 mt-0.5">
            {{ errors[name] }}
        </span>
    </div>
</template>