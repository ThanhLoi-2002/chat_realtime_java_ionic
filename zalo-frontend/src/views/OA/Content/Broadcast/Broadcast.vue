<template>
    <div class="h-full bg-gray-50 font-sans text-gray-800">
        <!-- MAIN CONTENT -->
        <main class="overflow-y-auto p-6">
            <!-- Top Header & Action Button -->
            <div class="flex justify-between items-center mb-6">
                <h1 class="text-2xl font-bold text-gray-900">Quản lý broadcast</h1>
                <button
                    class="inline-flex items-center px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded-lg shadow-sm transition-colors">
                    <i class="fas fa-circle-plus mr-2"></i>
                    Tạo broadcast
                </button>
            </div>

            <!-- Filter & Search Bar -->
            <div class="flex flex-wrap items-center justify-between gap-4 mb-6">
                <div class="flex flex-wrap items-center gap-3 flex-1">
                    <!-- Search Input -->
                    <div class="relative min-w-70">
                        <span
                            class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-400">
                            <i class="fas fa-search"></i>
                        </span>
                        <input type="text" placeholder="Tìm kiếm tên broadcast"
                            class="w-full pl-9 pr-4 py-2 bg-white border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                    </div>

                    <!-- Status Select -->
                    <div class="relative">
                        <select
                            class="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2 pr-8 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
                            <option>Tất cả trạng thái</option>
                            <option>Thành công</option>
                            <option>Đang gửi</option>
                            <option>Thất bại</option>
                        </select>
                        <span
                            class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-gray-400">
                            <i class="fas fa-chevron-down text-xs"></i>
                        </span>
                    </div>

                    <!-- Date Filter -->
                    <div
                        class="flex items-center bg-white border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-700 cursor-pointer hover:bg-gray-50">
                        <i class="fas fa-calendar mr-2 text-gray-400"></i>
                        <span>Lọc theo thời gian</span>
                    </div>
                </div>

                <!-- Export Stats -->
                <a href="#" class="text-sm text-blue-600 hover:underline">Xuất thống kê</a>
            </div>

            <!-- Data Table -->
            <div class="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden mb-4">
                <table class="w-full text-left border-collapse">
                    <thead>
                        <tr
                            class="bg-gray-50 border-b border-gray-200 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                            <th class="py-3 px-4 w-12">#</th>
                            <th class="py-3 px-4">Thời gian xuất bản</th>
                            <th class="py-3 px-4 w-1/3">Tên broadcast</th>
                            <th class="py-3 px-4">
                                Đã nhận <i class="fas fa-circle-question text-gray-400 ml-1"></i>
                            </th>
                            <th class="py-3 px-4">
                                Lượt xem <i class="fas fa-circle-question text-gray-400 ml-1"></i>
                            </th>
                            <th class="py-3 px-4">Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200 text-sm">
                        <tr v-for="item in broadcasts" :key="item.id" class="hover:bg-gray-50 transition-colors">
                            <td class="py-3 px-4 text-gray-500">{{ item.id }}</td>
                            <td class="py-3 px-4 text-gray-600 whitespace-nowrap">{{ item.publishedAt }}</td>
                            <td class="py-3 px-4">
                                <div class="flex items-center space-x-3">
                                    <img :src="item.thumbnail" alt="thumbnail"
                                        class="w-12 h-8 object-cover rounded border border-gray-200 shrink-0" />
                                    <span class="font-medium text-gray-900 line-clamp-1">{{ item.title }}</span>
                                </div>
                            </td>
                            <td class="py-3 px-4 text-gray-600">{{ item.receivedCount }}</td>
                            <td class="py-3 px-4 text-gray-600">{{ item.viewCount }}</td>
                            <td class="py-3 px-4">
                                <span
                                    class="inline-flex items-center px-2.5 py-0.5 rounded text-xs font-medium bg-green-100 text-green-700">
                                    {{ item.status }}
                                </span>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Pagination Footer -->
            <div class="flex items-center space-x-2 text-sm text-gray-600">
                <button
                    class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded hover:bg-gray-100 text-gray-400">
                    <i class="fas fa-chevron-left text-xs"></i>
                </button>
                <button
                    class="w-8 h-8 flex items-center justify-center border border-blue-500 bg-blue-50 text-blue-600 font-medium rounded">
                    1
                </button>
                <button
                    class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded hover:bg-gray-100">
                    <i class="fas fa-chevron-right text-xs"></i>
                </button>
            </div>
        </main>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface BroadcastItem {
    id: number;
    publishedAt: string;
    thumbnail: string;
    title: string;
    receivedCount: number;
    viewCount: number;
    status: string;
}

// Dữ liệu mẫu dựa trên hình ảnh
const broadcasts = ref<BroadcastItem[]>([
    {
        id: 1,
        publishedAt: '13/05/2024 12:20',
        thumbnail: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=100&auto=format&fit=crop&q=60',
        title: 'Tạo Cửa hàng Online trên Zalo Miễn Phí!',
        receivedCount: 118,
        viewCount: 5,
        status: 'Thành công'
    },
    {
        id: 2,
        publishedAt: '23/02/2024 16:40',
        thumbnail: 'https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=100&auto=format&fit=crop&q=60',
        title: 'Lì xì khai xuân',
        receivedCount: 43,
        viewCount: 2,
        status: 'Thành công'
    },
    {
        id: 3,
        publishedAt: '18/01/2024 11:20',
        thumbnail: 'https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=100&auto=format&fit=crop&q=60',
        title: '📢 THÔNG BÁO: ĐÀO TẠO VẬN HÀNH APP CHUYÊN NGHIỆP CHO HIỆP HỘI',
        receivedCount: 33,
        viewCount: 6,
        status: 'Thành công'
    },
    {
        id: 4,
        publishedAt: '03/01/2024 14:44',
        thumbnail: 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=100&auto=format&fit=crop&q=60',
        title: 'WEBINAR | TĂNG TRƯỞNG BỀN VỮNG TRONG KINH DOANH HỆ THỐNG',
        receivedCount: 30,
        viewCount: 7,
        status: 'Thành công'
    },
    {
        id: 5,
        publishedAt: '01/01/2024 16:19',
        thumbnail: 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=100&auto=format&fit=crop&q=60',
        title: 'WEBINAR | TĂNG TRƯỞNG BỀN VỮNG TRONG KINH DOANH HỆ THỐNG',
        receivedCount: 27,
        viewCount: 9,
        status: 'Thành công'
    }
]);
</script>