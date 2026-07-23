<template>
    <div class="flex h-full bg-gray-50 font-sans text-gray-800">
        <!-- MAIN CONTENT AREA -->
        <main class="flex-1 overflow-y-auto p-6 border-r border-gray-200">
            <!-- Top Header & Action Button -->
            <div class="flex justify-between items-center mb-6">
                <h1 class="text-2xl font-bold text-gray-900">Tạo broadcast</h1>
                <button
                    class="inline-flex items-center px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded-lg shadow-sm transition-colors">
                    <i class="fas fa-pen-to-square mr-2"></i>
                    Soạn bài viết mới
                </button>
            </div>

            <!-- Tabs (Bài viết / Video) -->
            <div class="flex border-b border-gray-200 mb-6 space-x-8 text-sm">
                <button @click="activeTab = 'post'" :class="[
                    'pb-3 font-medium transition-colors relative',
                    activeTab === 'post' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700'
                ]">
                    Bài viết
                </button>
                <button @click="activeTab = 'video'" :class="[
                    'pb-3 font-medium transition-colors relative',
                    activeTab === 'video' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700'
                ]">
                    Video
                </button>
            </div>

            <!-- Filters & Search Bar -->
            <div class="flex flex-wrap items-center justify-between gap-4 mb-6">
                <div class="flex items-center space-x-3 flex-1">
                    <!-- Search Input -->
                    <div class="relative w-80">
                        <span
                            class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-400">
                            <i class="fas fa-search"></i>
                        </span>
                        <input type="text" placeholder="Search tên bài viết"
                            class="w-full pl-9 pr-4 py-2 bg-white border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                    </div>

                    <!-- Date Filter -->
                    <div
                        class="flex items-center bg-white border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-700 cursor-pointer hover:bg-gray-50">
                        <i class="fas fa-calendar mr-2 text-gray-400"></i>
                        <span>Lọc theo thời gian</span>
                    </div>
                </div>
            </div>

            <!-- Data Table -->
            <div class="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden mb-6">
                <table class="w-full text-left border-collapse">
                    <thead>
                        <tr
                            class="bg-gray-50 border-b border-gray-200 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                            <th class="py-3 px-4 w-12">#</th>
                            <th class="py-3 px-4">Thời gian tạo</th>
                            <th class="py-3 px-4">Hình đại diện</th>
                            <th class="py-3 px-4 w-2/5">Tên bài viết</th>
                            <th class="py-3 px-4">Trạng thái</th>
                            <th class="py-3 px-4 text-right">Hành động</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200 text-sm">
                        <tr v-for="item in posts" :key="item.id" class="hover:bg-blue-50/50 transition-colors">
                            <td class="py-3 px-4 text-gray-500">{{ item.id }}</td>
                            <td class="py-3 px-4 text-gray-600 whitespace-nowrap">{{ item.createdAt }}</td>
                            <td class="py-3 px-4">
                                <img :src="item.thumbnail" alt="thumbnail"
                                    class="w-12 h-8 object-cover rounded border border-gray-200" />
                            </td>
                            <td class="py-3 px-4 font-medium text-gray-900">{{ item.title }}</td>
                            <td class="py-3 px-4 text-gray-600">{{ item.status }}</td>
                            <td class="py-3 px-4 text-right">
                                <button
                                    class="px-3 py-1 bg-white border border-gray-300 hover:bg-gray-50 text-gray-700 text-xs font-medium rounded shadow-sm transition-colors">
                                    Chọn
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Pagination & Row Count Footer -->
            <div class="flex items-center justify-between text-sm text-gray-600">
                <!-- Pagination -->
                <div class="flex items-center space-x-1">
                    <button
                        class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded hover:bg-gray-100 text-gray-400">
                        <i class="fas fa-chevron-left text-xs"></i>
                    </button>
                    <button
                        class="w-8 h-8 flex items-center justify-center border border-blue-500 bg-blue-50 text-blue-600 font-medium rounded">1</button>
                    <button
                        class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded hover:bg-gray-100">2</button>
                    <button
                        class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded hover:bg-gray-100">3</button>
                    <button
                        class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded hover:bg-gray-100">4</button>
                    <button
                        class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded hover:bg-gray-100 text-gray-400">
                        <i class="fas fa-chevron-right text-xs"></i>
                    </button>
                </div>

                <!-- Per Page Select -->
                <div class="flex items-center space-x-2">
                    <span>Hiển thị</span>
                    <select class="bg-white border border-gray-300 rounded px-2 py-1 text-sm focus:outline-none">
                        <option>10</option>
                        <option>20</option>
                        <option>50</option>
                    </select>
                    <span>mục/trang</span>
                </div>
            </div>
        </main>

        <!-- RIGHT SIDEBAR: Steps Guide -->
        <aside class="w-80 bg-white p-6 flex-col justify-between hidden xl:flex">
            <div>
                <!-- Illustration Placeholder -->
                <div class="flex justify-center mb-6">
                    <div
                        class="w-48 h-36 bg-blue-50 rounded-xl flex items-center justify-center relative overflow-hidden">
                        <!-- Icon minh họa tượng trưng cho banner bên phải -->
                        <i class="fas fa-envelope-open-text text-blue-400 text-6xl"></i>
                    </div>
                </div>

                <h3 class="font-bold text-gray-900 mb-4 text-base">Gửi Broadcast đến người quan tâm:</h3>

                <!-- Step List -->
                <div class="space-y-4">
                    <!-- Step 1 (Active) -->
                    <div class="flex items-start space-x-3">
                        <div
                            class="w-6 h-6 rounded bg-blue-600 text-white flex items-center justify-center text-xs font-bold shrink-0 mt-0.5">
                            1
                        </div>
                        <p class="text-sm text-gray-700 leading-snug">
                            Chọn nội dung (bài viết, video, sản phẩm)
                        </p>
                    </div>

                    <!-- Step 2 -->
                    <div class="flex items-start space-x-3">
                        <div
                            class="w-6 h-6 rounded bg-gray-200 text-gray-600 flex items-center justify-center text-xs font-bold shrink-0 mt-0.5">
                            2
                        </div>
                        <p class="text-sm text-gray-500 leading-snug">
                            Chọn đối tượng
                        </p>
                    </div>

                    <!-- Step 3 -->
                    <div class="flex items-start space-x-3">
                        <div
                            class="w-6 h-6 rounded bg-gray-200 text-gray-600 flex items-center justify-center text-xs font-bold shrink-0 mt-0.5">
                            3
                        </div>
                        <p class="text-sm text-gray-500 leading-snug">
                            Cài đặt thời gian gửi
                        </p>
                    </div>
                </div>
            </div>
        </aside>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface PostItem {
    id: number;
    createdAt: string;
    thumbnail: string;
    title: string;
    status: string;
}

const activeTab = ref<'post' | 'video'>('post');

// Dữ liệu mẫu tương ứng với giao diện
const posts = ref<PostItem[]>([
    {
        id: 1,
        createdAt: '30/05/2024 13:41',
        thumbnail: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=100&auto=format&fit=crop&q=60',
        title: 'Xây dựng thương hiệu uy tín với Zalo OA',
        status: 'Hiện'
    },
    {
        id: 2,
        createdAt: '11/05/2024 12:23',
        thumbnail: 'https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=100&auto=format&fit=crop&q=60',
        title: 'Tạo Cửa hàng Online trên Zalo Miễn Phí!',
        status: 'Hiện'
    },
    {
        id: 3,
        createdAt: '16/04/2024 10:19',
        thumbnail: 'https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=100&auto=format&fit=crop&q=60',
        title: '[ABAHA] HƯỚNG DẪN TẠO ZALO MINI APP MIỄN PHÍ CHỈ 1 PHÚT',
        status: 'Hiện'
    },
    {
        id: 4,
        createdAt: '07/03/2024 09:00',
        thumbnail: 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=100&auto=format&fit=crop&q=60',
        title: '✨ Mừng 8/3 - Ưu đãi "cực sốc" - Quà tặng "cực chất"!',
        status: 'Hiện'
    },
    {
        id: 5,
        createdAt: '06/03/2024 09:57',
        thumbnail: 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=100&auto=format&fit=crop&q=60',
        title: '😎 Mạng xã hội sập thì ta vẫn còn app riêng 😎',
        status: 'Hiện'
    }
]);
</script>