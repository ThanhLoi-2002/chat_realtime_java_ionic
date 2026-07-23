<template>
    <div class="flex h-full bg-gray-50 font-sans text-gray-800">
        <!-- LEFT: FORM CẤU HÌNH -->
        <main class="flex-1 overflow-y-auto p-8 border-r border-gray-200">
            <!-- Page Header -->
            <div class="mb-6">
                <h1 class="text-xl font-bold text-gray-900">Trang thông tin OA</h1>
                <p class="text-xs text-gray-500 mt-0.5">
                    Cài đặt thông tin hiển thị để khuyến khích người dùng tương tác và quan tâm OA nhiều hơn.
                </p>
            </div>

            <!-- Accordion Box: Thông tin tài khoản -->
            <div class="bg-white border border-gray-200 rounded-lg shadow-sm mb-4">
                <!-- Accordion Header -->
                <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center cursor-pointer">
                    <h2 class="text-sm font-bold text-gray-900">Thông tin tài khoản</h2>
                    <i class="fas fa-chevron-up text-xs text-gray-500"></i>
                </div>

                <!-- Accordion Body -->
                <div class="p-6 space-y-6">
                    <!-- Ảnh bìa -->
                    <div class="flex items-start justify-between">
                        <span class="text-sm text-gray-600 w-40 pt-2">Ảnh bìa</span>
                        <div class="flex-1 flex items-center space-x-4">
                            <img :src="form.coverImage" alt="Cover"
                                class="w-48 h-24 object-cover rounded border border-gray-200 shadow-sm" />
                            <input type="text" v-model="form.coverImage"
                                class="flex-1 px-3 py-1.5 border border-gray-300 rounded text-xs focus:ring-2 focus:ring-blue-500 focus:outline-none"
                                placeholder="Nhập URL ảnh bìa" />
                        </div>
                    </div>

                    <!-- Ảnh đại diện -->
                    <div class="flex items-start justify-between">
                        <span class="text-sm text-gray-600 w-40 pt-2">Ảnh đại diện</span>
                        <div class="flex-1 flex items-center space-x-4">
                            <img :src="form.avatar" alt="Avatar"
                                class="w-14 h-14 object-cover rounded-full border border-gray-200 shadow-sm" />
                            <input type="text" v-model="form.avatar"
                                class="flex-1 px-3 py-1.5 border border-gray-300 rounded text-xs focus:ring-2 focus:ring-blue-500 focus:outline-none"
                                placeholder="Nhập URL ảnh đại diện" />
                        </div>
                    </div>

                    <!-- Tên Official Account -->
                    <div class="flex items-center justify-between">
                        <span class="text-sm text-gray-600 w-40">Tên Official Account</span>
                        <div class="flex-1">
                            <input type="text" v-model="form.name"
                                class="w-full px-3 py-2 border border-gray-300 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none font-medium" />
                        </div>
                    </div>

                    <!-- Thông tin giới thiệu -->
                    <div class="flex items-start justify-between">
                        <span class="text-sm text-gray-600 w-40 pt-2">Thông tin giới thiệu</span>
                        <div class="flex-1">
                            <textarea v-model="form.description" rows="3"
                                class="w-full px-3 py-2 border border-gray-300 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"></textarea>
                        </div>
                    </div>

                    <!-- Nút Chỉnh sửa -->
                    <div class="flex justify-end pt-2">
                        <button
                            class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-medium rounded shadow transition-colors">
                            Chỉnh sửa
                        </button>
                    </div>
                </div>
            </div>

            <!-- Accordion Box: Thiết lập hiển thị -->
            <div class="bg-white border border-gray-200 rounded-lg shadow-sm mb-4">
                <div class="px-6 py-4 flex justify-between items-center cursor-pointer">
                    <h2 class="text-sm font-bold text-gray-900">Thiết lập hiển thị</h2>
                    <i class="fas fa-chevron-down text-xs text-gray-500"></i>
                </div>
            </div>

            <!-- Accordion Box: Thông tin doanh nghiệp -->
            <div class="bg-white border border-gray-200 rounded-lg shadow-sm">
                <div class="px-6 py-4 flex justify-between items-center cursor-pointer">
                    <h2 class="text-sm font-bold text-gray-900">Thông tin doanh nghiệp</h2>
                    <i class="fas fa-chevron-down text-xs text-gray-500"></i>
                </div>
            </div>
        </main>

        <!-- RIGHT: MÁY ẢO PREVIEW (Mô phỏng điện thoại) -->
        <aside class="w-95 bg-gray-100 p-6 flex flex-col items-center justify-center overflow-y-auto">
            <!-- Khung điện thoại -->
            <div
                class="w-75 h-145 bg-white rounded-[40px] border-8 border-gray-800 shadow-xl overflow-hidden flex flex-col relative">

                <!-- Status Bar điện thoại giả lập -->
                <div class="bg-blue-600 text-white px-4 py-1 text-[10px] flex justify-between items-center">
                    <span>10:07</span>
                    <div class="flex space-x-1.5 items-center">
                        <i class="fas fa-signal text-[8px]"></i>
                        <i class="fas fa-wifi text-[8px]"></i>
                        <i class="fas fa-battery-full text-[8px]"></i>
                    </div>
                </div>

                <!-- Header App mô phỏng Zalo -->
                <div class="bg-blue-600 text-white px-3 py-2 flex items-center justify-between text-xs font-medium">
                    <div class="flex items-center space-x-2">
                        <i class="fas fa-chevron-left"></i>
                        <span class="truncate max-w-45">{{ form.name }}</span>
                    </div>
                    <div class="flex space-x-3">
                        <i class="fas fa-ellipsis-h"></i>
                    </div>
                </div>

                <!-- Màn hình nội dung bên trong máy ảo -->
                <div class="flex-1 overflow-y-auto bg-gray-50 text-xs">
                    <!-- Ảnh bìa hiển thị real-time -->
                    <div class="relative h-28 bg-gray-200">
                        <img :src="form.coverImage" alt="Cover Preview" class="w-full h-full object-cover" />
                    </div>

                    <!-- Thông tin profile OA -->
                    <div class="bg-white px-4 pb-4 border-b border-gray-200 relative">
                        <!-- Ảnh đại diện chồng lên ảnh bìa -->
                        <div class="absolute -top-6 left-4">
                            <img :src="form.avatar" alt="Avatar Preview"
                                class="w-12 h-12 rounded-full border-2 border-white object-cover shadow-sm bg-white" />
                        </div>

                        <div class="pt-8">
                            <div class="flex items-center space-x-1">
                                <span class="font-bold text-gray-900 text-sm">{{ form.name }}</span>
                                <i class="fas fa-check-circle text-blue-500 text-[10px]"></i>
                            </div>
                            <p class="text-[10px] text-gray-500 mt-0.5">Doanh nghiệp</p>

                            <!-- Nút Quan tâm & Menu -->
                            <div class="flex items-center space-x-2 mt-3">
                                <button
                                    class="flex-1 bg-blue-600 text-white py-1.5 rounded-full font-medium text-center shadow-sm">
                                    Quan tâm
                                </button>
                                <button
                                    class="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600">
                                    <i class="fas fa-ellipsis"></i>
                                </button>
                            </div>

                            <div class="mt-2 text-[10px] text-gray-400 flex items-center space-x-1">
                                <i class="fas fa-shield-alt text-gray-400"></i>
                                <span>OA này đã được xác thực bởi Zalo</span>
                            </div>
                        </div>
                    </div>

                    <!-- Phần giới thiệu real-time -->
                    <div class="bg-white mt-2 p-3 border-t border-b border-gray-200">
                        <h4 class="font-bold text-gray-800 mb-1">Thông tin chi tiết</h4>
                        <p class="text-gray-600 leading-relaxed whitespace-pre-line">{{ form.description }}</p>

                        <div class="mt-3 space-y-1.5 text-gray-500">
                            <div class="flex items-start space-x-2">
                                <i class="fas fa-map-marker-alt mt-0.5 text-gray-400"></i>
                                <span>144 Khuất Duy Tiến, Nhân Chính, Thanh Xuân, Hà Nội</span>
                            </div>
                            <div class="flex items-center space-x-2">
                                <i class="fas fa-phone text-gray-400"></i>
                                <span>0927217227</span>
                            </div>
                            <div class="flex items-center space-x-2">
                                <i class="fas fa-clock text-gray-400"></i>
                                <span>Hoạt động 24/24</span>
                            </div>
                        </div>
                    </div>

                </div>

            </div>
        </aside>
    </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';

// Khai báo state liên kết dữ liệu 2 chiều từ Form sang máy ảo Preview
const form = reactive({
    name: 'Abaha Global',
    avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=60',
    coverImage: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=300&auto=format&fit=crop&q=60',
    description: 'Abaha là nền tảng thiết kế Mobile & Zalo Mini App thương mại điện tử. Abaha đã đóng gói trọn vẹn giải pháp cho các mô hình, bạn chỉ cần chọn mô hình phù hợp, chúng tôi triển khai trong 15 ngày.'
});
</script>