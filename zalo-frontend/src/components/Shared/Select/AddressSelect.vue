<template>
    <div class="flex-col w-full">
        <!-- Hàng chọn Tỉnh/Thành phố & Quận/Huyện -->
        <div class="grid grid-cols-2 gap-3">
            <!-- Chọn Tỉnh/Thành phố -->
            <div class="w-full">
                <select v-model="selectedProvince" :class="[
                    oaStyle.bg.secondary,
                    `${oaStyle.border.secondary} focus:ring-blue-400 focus:ring-1`,
                    oaStyle.text.secondary,
                    'w-full px-3 py-2 rounded border focus:outline-none'
                ]" @change="onProvinceChange">
                    <option :value="0" selected disabled>Chọn Tỉnh/Thành phố</option>
                    <option v-for="p in provinces" :key="p.code" :value="p.code">
                        {{ p.name }}
                    </option>
                </select>
            </div>

            <!-- Chọn Quận / Huyện -->
            <div class="w-full">
                <select v-model="selectedDistrict" :class="[
                    oaStyle.bg.secondary,
                    `${oaStyle.border.secondary} focus:ring-blue-400 focus:ring-1`,
                    oaStyle.text.secondary,
                    'w-full px-3 py-2 rounded border focus:outline-none'
                ]" :disabled="!selectedProvince">
                    <option :value="0" selected disabled>Chọn Quận/Huyện</option>
                    <option v-for="d in districts" :key="d.code" :value="d.code">
                        {{ d.name }}
                    </option>
                </select>
            </div>
        </div>

        <!-- Ô nhập chi tiết (Số nhà, tên đường, phường/xã) -->
        <div class="relative mt-4">
            <textarea v-model="selectedAddress" maxlength="100" rows="2"
                placeholder="Ví dụ: 16 Đặng Tất, Phường Tân Định" :class="[
                    oaStyle.bg.secondary,
                    `${oaStyle.border.secondary} focus:ring-blue-400 focus:ring-1`,
                    oaStyle.text.secondary,
                    'w-full px-3 py-2 rounded border focus:outline-none'
                ]" />
            <div class="flex justify-end text-sm mt-1">
                <div class="text-gray-400">
                    {{ selectedAddress?.length ?? 0 }}/100
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss'
import { ref, onMounted, watch } from 'vue'

// const props = defineProps<{
//     modelValue?: {
//         province: string | number | null
//         district: string | number | null
//         address: string
//     }
// }>()

const emit = defineEmits(['update:modelValue'])

const provinces = ref<any[]>([])
const districts = ref<any[]>([])

// Các biến nội bộ quản lý giá trị chọn
const selectedProvince = defineModel<number>('province', { default: 0 })
const selectedDistrict = defineModel<number>('district', { default: 0 })
const selectedAddress = defineModel<string>('address', { default: '' })

// Gọi API lấy danh sách Tỉnh/Thành phố khi component được mounted
onMounted(async () => {
    try {
        const res = await fetch('https://provinces.open-api.vn/api/?depth=1')
        provinces.value = await res.json()

        // Nếu có giá trị province truyền vào từ đầu (edit mode), tự động load danh sách huyện
        if (selectedProvince.value) {
            await fetchDistricts(selectedProvince.value)
        }
    } catch (error) {
        console.error('Lỗi tải danh sách tỉnh thành:', error)
    }
})

// Hàm gọi API lấy quận huyện theo tỉnh
const fetchDistricts = async (provinceCode: any) => {
    try {
        const res = await fetch(`https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`)
        const data = await res.json()
        districts.value = data.districts || []
    } catch (error) {
        console.error('Lỗi tải quận huyện:', error)
    }
}

// Khi đổi Tỉnh/Thành phố
const onProvinceChange = async () => {
    selectedDistrict.value = 0
    districts.value = []

    if (!selectedProvince.value) return
    await fetchDistricts(selectedProvince.value)
}
</script>