<script setup lang="ts">
import { ref } from 'vue'

// Định nghĩa kiểu dữ liệu cho một Official Account
interface OfficialAccount {
    id: number
    oaId: string
    name: string
    category: string
    status: string
    creator: string
    isVerified: boolean
    isAdvanced: boolean
}

// Trạng thái tab đang chọn
const activeTab = ref<string>('active')

// Danh sách các tab
const tabs = [
    { key: 'all', label: 'Tất cả' },
    { key: 'active', label: 'Đang hoạt động' },
    { key: 'pending', label: 'Đang chờ duyệt' },
    { key: 'locked', label: 'Đang bị khoá' },
]

// Dữ liệu mẫu dựa trên hình ảnh
const accounts = ref<OfficialAccount[]>([
    {
        id: 1,
        oaId: '3524915437534277108',
        name: 'Abaha Global',
        category: 'Doanh nghiệp',
        status: 'Đang hoạt động',
        creator: 'Quang',
        isVerified: true,
        isAdvanced: true,
    },
])

// Hàm xử lý khi bấm các nút
const handleCreateOA = () => {
    console.log('Tạo Official Account mới')
}

const handleRevokeAdmin = (id: number) => {
    console.log('Thôi làm Admin cho OA ID:', id)
}
</script>

<template>
    <div class="p-6 bg-gray-50 min-h-screen font-sans text-gray-800">
        <!-- Tiêu đề trang -->
        <h1 class="text-xl font-medium text-gray-700 mb-6">Quản lý Official Account</h1>

        <!-- Thanh điều hướng tab và nút tạo mới -->
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
            <!-- Tabs -->
            <div class="flex items-center space-x-6 text-sm">
                <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key" :class="[
                    'pb-1 transition-colors duration-200 font-medium',
                    activeTab === tab.key
                        ? 'text-blue-600 border-b-2 border-blue-600'
                        : 'text-gray-500 hover:text-gray-700'
                ]">
                    {{ tab.label }}
                </button>
            </div>

            <!-- Nút Tạo Official Account mới -->
            <button @click="handleCreateOA"
                class="bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium px-4 py-2 rounded shadow-sm transition-colors flex items-center justify-center gap-1">
                <span>Tạo Official Account mới</span>
            </button>
        </div>

        <!-- Bảng dữ liệu -->
        <div class="bg-white shadow-sm rounded-lg overflow-hidden border border-gray-100">
            <div class="overflow-x-auto">
                <table class="w-full text-left border-collapse">
                    <!-- Tiêu đề bảng -->
                    <thead>
                        <tr
                            class="bg-gray-50/70 border-b border-gray-200 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                            <th class="py-3 px-4 w-12 text-center">#</th>
                            <th class="py-3 px-4">OA ID</th>
                            <th class="py-3 px-4">Tên Official Account</th>
                            <th class="py-3 px-4">Danh mục chính</th>
                            <th class="py-3 px-4">Trạng thái</th>
                            <th class="py-3 px-4">Người tạo</th>
                            <th class="py-3 px-4 text-right">Thao tác</th>
                        </tr>
                    </thead>

                    <!-- Nội dung bảng -->
                    <tbody class="divide-y divide-gray-100 text-sm">
                        <tr v-for="item in accounts" :key="item.id" class="hover:bg-gray-50/50 transition-colors">
                            <!-- Cột ID STT -->
                            <td class="py-4 px-4 text-center text-gray-600 font-medium">{{ item.id }}</td>

                            <!-- Cột OA ID -->
                            <td class="py-4 px-4 text-gray-700 font-mono text-xs max-w-45 break-all">
                                {{ item.oaId }}
                            </td>

                            <!-- Cột Tên Official Account & Badge -->
                            <td class="py-4 px-4">
                                <div class="font-semibold text-blue-600 mb-1.5 cursor-pointer hover:underline">
                                    {{ item.name }}
                                </div>
                                <div class="flex items-center gap-2 flex-wrap">
                                    <!-- Badge Tài khoản xác thực -->
                                    <span v-if="item.isVerified"
                                        class="bg-blue-600 text-white text-[11px] font-medium px-2 py-0.5 rounded shadow-sm">
                                        Tài khoản xác thực
                                    </span>
                                    <!-- Badge Gói OA Nâng cao -->
                                    <span v-if="item.isAdvanced"
                                        class="bg-white border border-blue-400 text-blue-600 text-[11px] font-medium px-2 py-0.5 rounded">
                                        Gói OA Nâng cao
                                    </span>
                                </div>
                            </td>

                            <!-- Cột Danh mục chính -->
                            <td class="py-4 px-4 text-gray-600">{{ item.category }}</td>

                            <!-- Cột Trạng thái -->
                            <td class="py-4 px-4 text-gray-700">{{ item.status }}</td>

                            <!-- Cột Người tạo -->
                            <td class="py-4 px-4 text-gray-700">{{ item.creator }}</td>

                            <!-- Cột Thao tác -->
                            <td class="py-4 px-4 text-right">
                                <button @click="handleRevokeAdmin(item.id)"
                                    class="border border-gray-300 hover:bg-gray-100 text-gray-700 text-xs font-medium px-3 py-1.5 rounded transition-colors">
                                    Thôi làm Admin
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>