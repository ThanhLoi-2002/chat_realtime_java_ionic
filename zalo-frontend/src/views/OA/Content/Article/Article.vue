<template>
    <!-- MAIN CONTENT -->
    <main class="h-full bg-gray-50 text-gray-800 overflow-y-auto p-6">
        <!-- Top Header & Action Button -->
        <div class="flex justify-between items-center mb-6">
            <div class="text-xl font-medium text-gray-900">Quản lý bài viết</div>
            <button
                class="inline-flex items-center px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded-lg shadow-sm transition-colors cursor-pointer">
                <i class="fas fa-plus-circle mr-1.5"></i>
                Soạn bài viết mới
            </button>
        </div>

        <!-- Info Banner Notification -->
        <div
            class="flex items-center justify-between bg-blue-50 border border-blue-100 text-blue-800 px-4 py-3 rounded-lg mb-6 text-sm">
            <div class="flex items-center space-x-2">
                <i class="fas fa-info-circle"></i>
                <span>Đã xuất bản 0/15 nội dung</span>
            </div>
            <i class="fas fa-question-circle"></i>
        </div>

        <!-- Filter & Search Bar -->
        <div class="flex flex-wrap items-center justify-between gap-4 mb-6">
            <div class="flex flex-wrap items-center gap-3 flex-1">
                <!-- Search Input -->
                <div class="relative min-w-70">
                    <span class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-400">
                        <i class="fas fa-search"></i>
                    </span>
                    <input type="text" placeholder="Tìm kiếm bài viết"
                        class="w-full pl-9 pr-4 py-2 bg-white border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                </div>

                <!-- Status Select -->
                <div class="relative">
                    <select
                        class="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2 pr-8 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <option>Tất cả trạng thái</option>
                        <option>Đã xuất bản</option>
                        <option>Nháp</option>
                    </select>
                    <span class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-gray-400">
                        <i class="fas fa-chevron-down"></i>
                    </span>
                </div>

                <!-- Date Filter -->
                <div
                    class="flex items-center bg-white border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-700 cursor-pointer hover:bg-gray-50">
                    <i class="fas fa-calendar mr-1.5"></i>
                    <span>Lọc theo thời gian</span>
                </div>
            </div>

            <!-- Export Stats -->
            <a href="#" class="text-sm text-blue-600 hover:underline">Xuất thống kê</a>
        </div>

        <!-- Data Table -->
        <div class="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden">
            <table class="w-full text-left border-collapse">
                <thead>
                    <tr
                        class="bg-gray-50 border-b border-gray-200 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                        <th class="py-3 px-4 w-12">#</th>
                        <th class="py-3 px-4">Ngày xuất bản</th>
                        <th class="py-3 px-4">Hình đại diện</th>
                        <th class="py-3 px-4 w-1/3">Tên bài viết</th>
                        <th class="py-3 px-4 text-center">Lượt xem</th>
                        <th class="py-3 px-4 text-center">Chia sẻ</th>
                        <th class="py-3 px-4">Trạng thái</th>
                        <th class="py-3 px-4 text-right">Thao tác</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 text-sm">
                    <tr v-for="post in posts" :key="post.id" class="hover:bg-gray-50 transition-colors">
                        <td class="py-3 px-4 text-gray-500">{{ post.id }}</td>
                        <td class="py-3 px-4 text-gray-600 whitespace-nowrap">{{ post.publishedAt }}</td>
                        <td class="py-3 px-4">
                            <img :src="post.thumbnail" alt="thumbnail"
                                class="w-12 h-8 object-cover rounded border border-gray-200" />
                        </td>
                        <td class="py-3 px-4 font-medium text-gray-900">{{ post.title }}</td>
                        <td class="py-3 px-4 text-center text-gray-600">{{ post.views }}</td>
                        <td class="py-3 px-4 text-center text-gray-600">{{ post.shares }}</td>
                        <td class="py-3 px-4">
                            <span :class="[
                                'inline-flex items-center px-2.5 py-0.5 rounded text-xs font-medium',
                                post.status === 'published' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'
                            ]">
                                {{ post.status === 'published' ? 'Đã xuất bản' : 'Nháp' }}
                            </span>
                        </td>
                        <td class="py-3 px-4 text-right">
                            <button class="text-gray-400 hover:text-gray-600">
                                <i class="fas fa-ellipsis-h"></i>
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </main>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface Post {
    id: number;
    publishedAt: string;
    thumbnail: string;
    title: string;
    views: number;
    shares: number;
    status: 'published' | 'draft';
}

// Dữ liệu mẫu dựa trên giao diện mẫu
const posts = ref<Post[]>([
    {
        id: 1,
        publishedAt: '30/05/2024 13:41',
        thumbnail: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=100&auto=format&fit=crop&q=60',
        title: 'Xây dựng thương hiệu uy tín với Zalo OA',
        views: 1,
        shares: 0,
        status: 'published'
    },
    {
        id: 2,
        publishedAt: '11/05/2024 12:23',
        thumbnail: 'https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=100&auto=format&fit=crop&q=60',
        title: 'Tạo Cửa hàng Online trên Zalo Miễn Phí!',
        views: 18,
        shares: 1,
        status: 'published'
    },
    {
        id: 3,
        publishedAt: '11/05/2024 11:24',
        thumbnail: 'https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=100&auto=format&fit=crop&q=60',
        title: 'Tạo của hàng online trên Zalo miễn phí',
        views: 1,
        shares: 0,
        status: 'draft'
    },
    {
        id: 4,
        publishedAt: '11/05/2024 10:11',
        thumbnail: 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=100&auto=format&fit=crop&q=60',
        title: 'Tạo của hàng online trên Zalo miễn phí',
        views: 1,
        shares: 0,
        status: 'draft'
    },
    {
        id: 5,
        publishedAt: '16/04/2024 10:19',
        thumbnail: 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=100&auto=format&fit=crop&q=60',
        title: '[ABAHA] HƯỚNG DẪN TẠO ZALO MINI APP MIỄN PHÍ CHỈ 1 PHÚT',
        views: 4,
        shares: 0,
        status: 'published'
    }
]);
</script>