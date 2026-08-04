<template>
    <div :class="[direction === 'vertical' ? 'flex-col' : 'justify-between items-start', 'flex gap-2']">
        <label :class="[oaStyle.text.primary, direction === 'vertical' ? 'w-full' : 'w-1/3']">{{ t(label) }} <span
                class="text-red-500">*</span></label>

        <div class="flex-col w-full">
            <!-- Hàng chọn Tỉnh/Thành phố & Quận/Huyện (Đổi space-y-4 thành gap-3 hoặc gap-4) -->
            <div class="grid grid-cols-2 gap-3">
                <div class="w-full">
                    <select :id="names[0]" :name="names[0]" v-model="province" v-bind="provinceAttrs" :class="[
                        oaStyle.bg.primary,
                        errors[names[0]] ? 'border-red-500' : `${oaStyle.border.primary} focus:ring-blue-400 focus:ring-1`,
                        oaStyle.text.primary,
                        'w-full px-3 py-2 rounded-md border focus:outline-none'
                    ]" @change="onProvinceChange">
                        <option value="undefined" selected disabled>Chọn Tỉnh/Thành phố</option>
                        <option v-for="p in provinces" :key="p.code" :value="p.code">
                            {{ p.name }}
                        </option>
                    </select>

                    <span v-if="errors[names[0]]" class="text-red-500 text-sm mt-1 block">
                        {{ t(errors[names[0]]) }}
                    </span>
                </div>

                <!-- Chọn Quận / Huyện -->
                <div class="w-full">
                    <select :id="names[1]" :name="names[1]" v-model="district" v-bind="districtAttrs" :class="[
                        oaStyle.bg.primary,
                        errors[names[1]] ? 'border-red-500' : `${oaStyle.border.primary} focus:ring-blue-400 focus:ring-1`,
                        oaStyle.text.primary,
                        'w-full px-3 py-2 rounded-md border focus:outline-none'
                    ]" :disabled="!province">
                        <option value="undefined" selected disabled>Chọn Quận/Huyện</option>
                        <option v-for="p in districts" :key="p.code" :value="p.code">
                            {{ p.name }}
                        </option>
                    </select>

                    <span v-if="errors[names[0]]" class="text-red-500 text-sm mt-1 block">
                        {{ t(errors[names[0]]) }}
                    </span>
                </div>
            </div>

            <!-- Ô nhập chi tiết (Số nhà, tên đường, phường/xã) -->
            <div class="relative mt-4">
                <textarea type="text" v-model="address" v-bind="addressAttrs" maxlength="100" rows="2"
                    placeholder="Ví dụ: 16 Đặng Tất, Phường Tân Định" :class="[
                        oaStyle.bg.primary,
                        errors[names[2]] ? 'border-red-500' : `${oaStyle.border.primary} focus:ring-blue-400 focus:ring-1`,
                        oaStyle.text.primary,
                        'w-full px-3 py-2 rounded-md border focus:outline-none'
                    ]" />
                <div class="flex justify-between gap-2 text-sm mt-1">
                    <span v-if="errors[names[2]]" class="text-red-500">
                        {{ t(errors[names[2]]) }}
                    </span>
                    <div class="text-gray-400">
                        {{ address?.length ?? 0 }}/100
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss'
import { useTranslate } from '@/composables/useTranslate';
import { ref, onMounted, watch } from 'vue'

const props = defineProps<{
    label: string
    defineField: any
    names: string[]
    errors: any
    schema: any
    isTextarea?: boolean
    placeholder?: string
    direction: 'horizontal' | 'vertical'
}>()

const { t } = useTranslate()
const provinces = ref<any[]>([])
const districts = ref<any[]>([])

const [province, provinceAttrs] = props.defineField(props.names[0])
const [district, districtAttrs] = props.defineField(props.names[1])
const [address, addressAttrs] = props.defineField(props.names[2])

const emit = defineEmits(['update:address'])

// Gọi API lấy danh sách Tỉnh/Thành phố khi component được mounted
onMounted(async () => {
    try {
        const res = await fetch('https://provinces.open-api.vn/api/?depth=1')
        provinces.value = await res.json()
    } catch (error) {
        console.error('Lỗi tải danh sách tỉnh thành:', error)
    }
})

// Khi đổi Tỉnh/Thành phố -> Lấy danh sách Quận/Huyện tương ứng
const onProvinceChange = async () => {
    district.value = undefined
    districts.value = []

    if (!province.value) return

    try {
        const res = await fetch(`https://provinces.open-api.vn/api/p/${province.value}?depth=2`)
        const data = await res.json()
        districts.value = data.districts || []
    } catch (error) {
        console.error('Lỗi tải quận huyện:', error)
    }
}

// Gom dữ liệu trả về cho component cha mỗi khi có thay đổi
watch([province, district, address], () => {
    const provinceObj = provinces.value.find(p => p.code === province.value)
    const districtObj = districts.value.find(d => d.code === district.value)

    emit('update:address', {
        province: provinceObj?.name || '',
        district: districtObj?.name || '',
        detail: address.value,
        fullAddress: `${address.value}, ${districtObj?.name || ''}, ${provinceObj?.name || ''}`.replace(/^, /, '')
    })
})
</script>