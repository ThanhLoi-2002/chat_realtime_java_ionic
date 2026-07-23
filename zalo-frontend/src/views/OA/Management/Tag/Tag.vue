<template>
    <div class="flex h-full bg-gray-50 text-gray-800">
        <!-- SIDEBAR -->
        <aside class="w-64 bg-white border-r border-gray-200 flex flex-col justify-between">
            <div class="py-4">
                <nav class="space-y-1">
                    <a href="#" class="flex items-center px-4 py-2.5 text-sm text-gray-600 hover:bg-gray-100">
                        <font-awesome-icon :icon="['fas', 'info-circle']" class="w-5 h-5 mr-3 text-gray-400" />
                        Trang thông tin OA
                    </a>
                    <a href="#"
                        class="flex items-center px-4 py-2.5 text-sm text-gray-600 hover:bg-gray-100 justify-between">
                        <div class="flex items-center">
                            <font-awesome-icon :icon="['fas', 'sliders']" class="w-5 h-5 mr-3 text-gray-400" />
                            Thiết lập tương tác
                        </div>
                        <font-awesome-icon :icon="['fas', 'chevron-down']" class="w-3 h-3 text-gray-400" />
                    </a>
                    <a href="#" class="flex items-center px-4 py-2.5 text-sm text-gray-600 hover:bg-gray-100">
                        <font-awesome-icon :icon="['fas', 'phone']" class="w-5 h-5 mr-3 text-gray-400" />
                        Thiết lập cuộc gọi
                    </a>
                    <a href="#" class="flex items-center px-4 py-2.5 text-sm text-gray-600 hover:bg-gray-100">
                        <font-awesome-icon :icon="['fas', 'user']" class="w-5 h-5 mr-3 text-gray-400" />
                        Người dùng
                    </a>
                    <!-- Active Menu Item -->
                    <a href="#"
                        class="flex items-center px-4 py-2.5 text-sm font-medium text-blue-600 bg-blue-50 border-r-4 border-blue-600">
                        <font-awesome-icon :icon="['fas', 'tag']" class="w-5 h-5 mr-3 text-blue-600" />
                        Quản lý nhãn
                    </a>
                    <a href="#" class="flex items-center px-4 py-2.5 text-sm text-gray-600 hover:bg-gray-100">
                        <font-awesome-icon :icon="['fas', 'user-shield']" class="w-5 h-5 mr-3 text-gray-400" />
                        Quản lý admin
                    </a>
                    <a href="#"
                        class="flex items-center px-4 py-2.5 text-sm text-gray-600 hover:bg-gray-100 justify-between">
                        <div class="flex items-center">
                            <font-awesome-icon :icon="['fas', 'wallet']" class="w-5 h-5 mr-3 text-gray-400" />
                            Quản lý tài khoản
                        </div>
                        <font-awesome-icon :icon="['fas', 'chevron-down']" class="w-3 h-3 text-gray-400" />
                    </a>
                    <a href="#" class="flex items-center px-4 py-2.5 text-sm text-gray-600 hover:bg-gray-100">
                        <font-awesome-icon :icon="['fas', 'bell']" class="w-5 h-5 mr-3 text-gray-400" />
                        Tin nhắn hệ thống
                    </a>
                    <a href="#" class="flex items-center px-4 py-2.5 text-sm text-gray-600 hover:bg-gray-100">
                        <font-awesome-icon :icon="['fas', 'history']" class="w-5 h-5 mr-3 text-gray-400" />
                        Lịch sử hoạt động
                    </a>
                </nav>
            </div>

            <!-- Footer Collapse Button -->
            <div class="p-4 border-t border-gray-200">
                <button class="text-gray-500 hover:text-gray-700">
                    <font-awesome-icon :icon="['fas', 'angles-left']" class="w-5 h-5" />
                </button>
            </div>
        </aside>

        <!-- MAIN CONTENT -->
        <main class="flex-1 overflow-y-auto p-8">
            <!-- Page Header -->
            <div class="mb-6">
                <h1 class="text-2xl font-bold text-gray-900">Quản lý nhãn</h1>
                <p class="text-sm text-gray-500 mt-1">
                    Tạo và tùy chỉnh Nhãn để sắp xếp hoặc phân loại khách hàng theo nhu cầu của doanh nghiệp.
                </p>
            </div>

            <!-- SECTION 1: Nhãn quy trình -->
            <div class="mb-8">
                <div class="flex items-center space-x-2 mb-3">
                    <h2 class="text-base font-semibold text-gray-800">Nhãn quy trình</h2>
                    <font-awesome-icon :icon="['fas', 'circle-question']"
                        class="text-gray-400 w-4 h-4 cursor-pointer" />
                </div>

                <!-- Card Nhãn quy trình -->
                <div class="bg-white border border-gray-200 rounded-lg shadow-sm">
                    <!-- Card Header -->
                    <div class="px-4 py-3 border-b border-gray-200 flex items-center text-sm font-medium text-gray-700">
                        <font-awesome-icon :icon="['fas', 'bars']" class="mr-2 text-gray-400 w-4 h-4" />
                        Quy trình
                    </div>

                    <!-- Card Body / List Items -->
                    <div class="p-4">
                        <div v-for="tag in processTags" :key="tag.id"
                            class="flex items-center justify-between py-2 border-b border-gray-100 last:border-none">
                            <div class="flex items-center space-x-3">
                                <font-awesome-icon :icon="['fas', 'bars']" class="text-gray-300 w-4 h-4 cursor-grab" />
                                <font-awesome-icon :icon="['fas', 'circle']" :style="{ color: tag.color }"
                                    class="w-3 h-3" />
                                <span class="text-sm text-gray-800 font-medium">{{ tag.name }}</span>
                                <font-awesome-icon :icon="['fas', 'circle-question']"
                                    class="text-gray-400 w-3.5 h-3.5" />
                            </div>

                            <div class="flex items-center space-x-4 text-gray-500">
                                <span class="text-xs">{{ tag.count }}</span>
                                <button class="hover:text-blue-600 transition-colors">
                                    <font-awesome-icon :icon="['fas', 'pen']" class="w-3.5 h-3.5" />
                                </button>
                                <button class="hover:text-red-600 transition-colors">
                                    <font-awesome-icon :icon="['fas', 'trash-can']" class="w-3.5 h-3.5" />
                                </button>
                            </div>
                        </div>

                        <!-- Add Tag Button Inside Card -->
                        <div class="mt-4">
                            <button
                                class="inline-flex items-center px-3 py-1.5 border border-blue-500 text-blue-600 rounded text-xs font-medium hover:bg-blue-50 transition-colors">
                                <font-awesome-icon :icon="['fas', 'plus']" class="mr-1.5 w-3 h-3" />
                                Thêm nhãn
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- SECTION 2: Nhãn thường -->
            <div>
                <div class="flex items-center space-x-2 mb-3">
                    <h2 class="text-base font-semibold text-gray-800">Nhãn thường</h2>
                    <font-awesome-icon :icon="['fas', 'circle-question']"
                        class="text-gray-400 w-4 h-4 cursor-pointer" />
                </div>

                <!-- Add Group Button -->
                <button
                    class="mb-4 inline-flex items-center px-3.5 py-1.5 bg-blue-600 text-white rounded text-xs font-medium hover:bg-blue-700 transition-colors shadow-sm">
                    <font-awesome-icon :icon="['fas', 'plus']" class="mr-1.5 w-3 h-3" />
                    Thêm nhóm
                </button>

                <!-- Card Nhãn thường -->
                <div class="bg-white border border-gray-200 rounded-lg shadow-sm">
                    <!-- Card Header -->
                    <div class="px-4 py-3 border-b border-gray-200 flex items-center text-sm font-medium text-gray-700">
                        <font-awesome-icon :icon="['fas', 'tag']" class="mr-2 text-gray-400 w-4 h-4" />
                        Nhóm khách hàng
                    </div>

                    <!-- Card Body -->
                    <div class="p-4">
                        <!-- Add Tag Button Inside Card -->
                        <div>
                            <button
                                class="inline-flex items-center px-3 py-1.5 border border-blue-500 text-blue-600 rounded text-xs font-medium hover:bg-blue-50 transition-colors">
                                <font-awesome-icon :icon="['fas', 'plus']" class="mr-1.5 w-3 h-3" />
                                Thêm nhãn
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface ProcessTag {
    id: number;
    name: string;
    color: string;
    count: number;
}

// Khai báo dữ liệu mẫu cho Nhãn quy trình
const processTags = ref<ProcessTag[]>([
    { id: 1, name: 'Hoàn thành', color: '#10B981', count: 0 } // Màu xanh lá tương ứng icon trong ảnh
]);
</script>