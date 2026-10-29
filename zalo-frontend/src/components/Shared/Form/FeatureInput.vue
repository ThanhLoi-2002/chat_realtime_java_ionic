<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { FeatureDTO } from '@/types/entities';
import { computed } from 'vue'

const props = defineProps<{
    name: string
    label?: string
    errors: Record<string, any>
    defineField: (field: any) => [any, Record<string, any>]
    direction?: 'vertical' | 'horizontal'
}>()

// Bind trực tiếp với Vee-Validate
const [features] = props.defineField(props.name)
const { t } = useTranslate()

// Giá trị danh sách tính năng
const featureList = computed<FeatureDTO[]>({
    get() {
        if (Array.isArray(features.value) && features.value.length > 0) {
            return features.value
        }
        return [{ text: '', tag: '', subtext: '' }]
    },
    set(val) {
        features.value = val
    }
})

// Hàm cập nhật dữ liệu của 1 dòng
const updateRow = (index: number, key: keyof FeatureDTO, value: string) => {
    const newList = [...featureList.value]
    newList[index] = { ...newList[index], [key]: value }
    featureList.value = newList
}

// Thêm dòng tính năng mới
const addRow = () => {
    featureList.value = [...featureList.value, { text: '', tag: '', subtext: '' }]
}

// Xóa 1 dòng tính năng
const removeRow = (index: number) => {
    const newList = featureList.value.filter((_, i) => i !== index)
    featureList.value = newList.length > 0 ? newList : [{ text: '', tag: '', subtext: '' }]
}
</script>

<template>
    <div :class="['flex', direction === 'vertical' ? 'flex-col gap-1.5' : 'flex-row items-center gap-4']">
        <!-- Label và Nút Thêm -->
        <div class="flex items-center justify-between">
            <label v-if="label" class="text-sm font-semibold capitalize text-slate-300">
                {{ t(label) }} <span class="text-red-500">*</span>
            </label>
            <button type="button" @click="addRow"
                class="text-xs font-semibold text-blue-400 hover:text-blue-300 flex items-center gap-1 cursor-pointer">
                <i class="fas fa-plus"></i> {{ t('Thêm tính năng') }}
            </button>
        </div>

        <!-- Danh sách các dòng nhập -->
        <div class="space-y-3 pr-1">
            <div v-for="(item, index) in featureList" :key="index"
                class="p-2.5 bg-slate-900 border border-slate-700 rounded-lg space-y-2 relative group">
                <!-- Dòng 1: Ô nhập Tên tính năng + Tag Badge + Nút Xóa -->
                <div class="flex items-center gap-2">
                    <input type="text" :value="item.text"
                        @input="(e) => updateRow(index, 'text', (e.target as HTMLInputElement).value)"
                        placeholder="Tên tính năng (VD: Nhãn nhận diện zBusiness)..."
                        class="flex-1 bg-slate-800 border border-slate-600 rounded px-2.5 py-1 text-sm text-slate-100 focus:outline-none focus:border-blue-500" />

                    <input type="text" :value="item.tag"
                        @input="(e) => updateRow(index, 'tag', (e.target as HTMLInputElement).value)"
                        placeholder="Tag (Mới, Business...)"
                        class="w-32 bg-slate-800 border border-slate-600 rounded px-2.5 py-1 text-sm text-amber-400 focus:outline-none focus:border-blue-500" />

                    <button type="button" @click="removeRow(index)"
                        class="text-slate-400 hover:text-red-400 px-1 text-sm transition-colors cursor-pointer"
                        title="Xóa dòng này">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </div>

                <!-- Dòng 2: Ô nhập Mô tả phụ (Subtext) -->
                <input type="text" :value="item.subtext"
                    @input="(e) => updateRow(index, 'subtext', (e.target as HTMLInputElement).value)"
                    placeholder="Mô tả phụ nghiêng bên dưới (nếu có)..."
                    class="w-full bg-slate-800/50 border border-slate-700 rounded px-2.5 py-1 text-xs text-slate-400 italic focus:outline-none focus:border-blue-500" />
            </div>
        </div>

        <!-- Báo lỗi từ schema nếu có -->
        <span v-if="errors && errors[name]" class="text-xs text-red-500 mt-0.5">
            {{ errors[name] }}
        </span>
    </div>
</template>