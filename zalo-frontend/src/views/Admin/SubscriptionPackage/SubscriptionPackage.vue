<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { oaStyle } from '@/assets/tailwindcss'

interface SubscriptionPackage {
    id?: number
    name: string
    code: string
    price: number
    durationDays: number
    maxMembers: number
    description: string
}

const packages = ref < SubscriptionPackage[] > ([])
const showModal = ref(false)
const isEditing = ref(false)

// Form state
const form = ref < SubscriptionPackage > ({
    name: '',
    code: '',
    price: 0,
    durationDays: 30,
    maxMembers: 100,
    description: ''
})

// Lấy danh sách gói cước
const fetchPackages = async () => {
    //   try {
    //     const res = await axios.get('/api/admin/packages')
    //     packages.value = res.data
    //   } catch (err) {
    //     console.error('Lỗi tải danh sách gói cước:', err)
    //   }
}

// Mở modal thêm mới
const handleOpenCreate = () => {
    isEditing.value = false
    form.value = { name: '', code: '', price: 0, durationDays: 30, maxMembers: 100, description: '' }
    showModal.value = true
}

// Mở modal chỉnh sửa
const handleOpenEdit = (pkg: SubscriptionPackage) => {
    isEditing.value = true
    form.value = { ...pkg }
    showModal.value = true
}

// Lưu dữ liệu (Thêm mới hoặc Cập nhật)
const handleSave = async () => {
    //   try {
    //     if (isEditing.value && form.value.id) {
    //       await axios.put(`/api/admin/packages/${form.value.id}`, form.value)
    //     } else {
    //       await axios.post('/api/admin/packages', form.value)
    //     }
    //     showModal.value = false
    //     fetchPackages()
    //   } catch (err) {
    //     alert('Có lỗi xảy ra khi lưu gói cước!')
    //   }
}

// Xóa gói cước
const handleDelete = async (id?: number) => {
    if (!id) return
    if (confirm('Bạn có chắc chắn muốn xóa gói cước này không?')) {
        // try {
        //   await axios.delete(`/api/admin/packages/${id}`)
        //   fetchPackages()
        // } catch (err) {
        //   alert('Không thể xóa gói cước này.')
        // }
    }
}

onMounted(() => {
    fetchPackages()
})
</script>

<template>
    <div :class="['p-6']">
        <!-- Header -->
        <div class="flex justify-between items-center mb-8">
            <div>
                <h1 class="text-2xl font-black text-slate-900">Quản trị Gói dịch vụ (Packages)</h1>
                <p class="text-sm text-slate-500">Quản lý các gói cước nâng cấp nhóm / cộng đồng doanh nghiệp.</p>
            </div>
            <button @click="handleOpenCreate"
                class="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl shadow-md transition-colors flex items-center gap-2">
                <span>+ Thêm gói mới</span>
            </button>
        </div>

        <!-- Bảng danh sách gói cước -->
        <div class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
            <table class="w-full text-left border-collapse">
                <thead>
                    <tr
                        class="bg-slate-50 border-b border-slate-200 text-xs font-bold text-slate-500 uppercase tracking-wider">
                        <th class="p-4">Tên gói</th>
                        <th class="p-4">Mã Code</th>
                        <th class="p-4">Giá tiền (VND)</th>
                        <th class="p-4">Thời hạn (Ngày)</th>
                        <th class="p-4">Giới hạn thành viên</th>
                        <th class="p-4 text-right">Hành động</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-100 text-sm text-slate-700">
                    <tr v-for="pkg in packages" :key="pkg.id" class="hover:bg-slate-50/50 transition-colors">
                        <td class="p-4 font-bold text-slate-900">{{ pkg.name }}</td>
                        <td class="p-4"><span
                                class="px-2.5 py-1 bg-slate-100 font-mono text-xs rounded-md text-slate-600">{{ pkg.code
                                }}</span></td>
                        <td class="p-4 font-semibold text-blue-600">{{ pkg.price.toLocaleString('vi-VN') }}đ</td>
                        <td class="p-4">{{ pkg.durationDays }} ngày</td>
                        <td class="p-4">{{ pkg.maxMembers }} thành viên</td>
                        <td class="p-4 text-right space-x-2">
                            <button @click="handleOpenEdit(pkg)"
                                class="px-3 py-1.5 bg-amber-50 hover:bg-amber-100 text-amber-600 font-semibold rounded-lg transition-colors text-xs">
                                Sửa
                            </button>
                            <button @click="handleDelete(pkg.id)"
                                class="px-3 py-1.5 bg-red-50 hover:bg-red-100 text-red-600 font-semibold rounded-lg transition-colors text-xs">
                                Xóa
                            </button>
                        </td>
                    </tr>
                    <tr v-if="packages.length === 0">
                        <td colspan="6" class="p-8 text-center text-slate-400">Chưa có gói cước nào được tạo.</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- Modal Thêm / Sửa -->
        <div v-if="showModal"
            class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
            <div class="bg-white rounded-3xl max-w-lg w-full p-6 shadow-2xl relative">
                <h3 class="text-xl font-bold text-slate-900 mb-4">
                    {{ isEditing ? 'Chỉnh sửa gói cước' : 'Thêm gói cước mới' }}
                </h3>

                <form @submit.prevent="handleSave" class="space-y-4">
                    <div>
                        <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Tên gói cước</label>
                        <input v-model="form.name" type="text" required placeholder="VD: Gói Cộng Đồng Pro"
                            class="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none text-sm" />
                    </div>

                    <div class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Mã Code định
                                danh</label>
                            <input v-model="form.code" type="text" required placeholder="VD: MONTHLY"
                                class="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none text-sm font-mono uppercase" />
                        </div>
                        <div>
                            <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Giá tiền (VND)</label>
                            <input v-model.number="form.price" type="number" required min="0"
                                class="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none text-sm" />
                        </div>
                    </div>

                    <div class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Thời hạn (Số
                                ngày)</label>
                            <input v-model.number="form.durationDays" type="number" required min="1"
                                class="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none text-sm" />
                        </div>
                        <div>
                            <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Giới hạn thành
                                viên</label>
                            <input v-model.number="form.maxMembers" type="number" required min="1"
                                class="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none text-sm" />
                        </div>
                    </div>

                    <div>
                        <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Mô tả tính năng</label>
                        <textarea v-model="form.description" rows="3" placeholder="Nhập mô tả tính năng đi kèm..."
                            class="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none text-sm"></textarea>
                    </div>

                    <div class="flex justify-end gap-3 pt-4 border-t border-slate-100">
                        <button type="button" @click="showModal = false"
                            class="px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold rounded-xl transition-colors text-sm">
                            Hủy
                        </button>
                        <button type="submit"
                            class="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl transition-colors text-sm shadow-md">
                            Lưu thông tin
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>