<template>
    <div :class="[oaStyle.bg.primary, 'flex h-full']">
        <!-- LEFT: FORM CẤU HÌNH -->
        <main :class="[oaStyle.border.secondary, 'flex-1 overflow-y-auto p-8 border-r']">
            <!-- Page Header -->
            <div class="mb-6">
                <div :class="[oaStyle.text.primary, 'text-xl font-medium']">Trang thông tin OA</div>
                <p :class="[oaStyle.text.secondary, 'text-xs mt-0.5']">
                    Cài đặt thông tin hiển thị để khuyến khích người dùng tương tác và quan tâm OA nhiều hơn.
                </p>
            </div>

            <!-- Accordion Box: Thông tin tài khoản -->
            <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'border rounded-lg shadow-sm mb-4']">
                <!-- Accordion Header -->
                <div @click="toggleSection('account')"
                    :class="[oaStyle.border.secondary, 'px-6 py-4 border-b flex justify-between items-center cursor-pointer']">
                    <div :class="[oaStyle.text.primary, 'text-md font-medium']">Thông tin tài khoản</div>
                    <i
                        :class="[oaStyle.text.secondary, sections.account ? 'fas fa-chevron-up text-xs' : 'fas fa-chevron-down text-xs']"></i>
                </div>

                <!-- Accordion Body -->
                <div v-show="sections.account" class="p-6 space-y-6">
                    <!-- Ảnh bìa -->
                    <div class="flex items-start justify-between">
                        <span :class="[oaStyle.text.primary, 'text-sm w-40 pt-2']">Ảnh bìa</span>
                        <div class="flex-1 flex items-center space-x-4">
                            <img :src="form.coverImage" alt="Cover"
                                :class="[oaStyle.border.secondary, 'w-48 h-24 object-cover rounded border shadow-sm']" />
                        </div>
                    </div>

                    <!-- Ảnh đại diện -->
                    <div class="flex items-start justify-between">
                        <span :class="[oaStyle.text.primary, 'text-sm w-40 pt-2']">Ảnh đại diện</span>
                        <div class="flex-1 flex items-center space-x-4">
                            <img :src="form.avatar" alt="Avatar"
                                :class="[oaStyle.border.secondary, 'w-14 h-14 object-cover rounded-full border shadow-sm']" />
                        </div>
                    </div>

                    <!-- Tên Official Account -->
                    <div class="flex items-center justify-between">
                        <span :class="[oaStyle.text.primary, 'text-sm w-40']">Tên Official Account</span>
                        <div class="flex-1">
                            <input type="text" v-model="form.name"
                                :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'w-full px-3 py-2 border rounded text-sm focus:ring-1 focus:ring-slate-500 focus:outline-none font-medium']" />
                        </div>
                    </div>

                    <!-- Thông tin giới thiệu -->
                    <div class="flex items-start justify-between">
                        <span :class="[oaStyle.text.primary, 'text-sm w-40 pt-2']">Thông tin giới thiệu</span>
                        <div class="flex-1">
                            <textarea v-model="form.description" rows="3"
                                :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'w-full px-3 py-2 border rounded text-sm focus:ring-1 focus:ring-slate-500 focus:outline-none']"></textarea>
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
            <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'border rounded-lg shadow-sm mb-4']">
                <div @click="toggleSection('display')"
                    :class="[sections.display ? 'border-b' : '', oaStyle.border.secondary, 'px-6 py-4 flex justify-between items-center cursor-pointer']">
                    <div :class="[oaStyle.text.primary, 'text-md font-medium']">Thiết lập hiển thị</div>
                    <i
                        :class="[oaStyle.text.secondary, sections.display ? 'fas fa-chevron-up text-xs' : 'fas fa-chevron-down text-xs']"></i>
                </div>
                <div v-show="sections.display" class="p-6 space-y-4">
                    <div class="flex items-center justify-between">
                        <span :class="[oaStyle.text.primary, 'text-sm']">Hiển thị nút gọi nhanh</span>
                        <input type="checkbox" v-model="form.showCallButton"
                            class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
                    </div>
                    <div class="flex items-center justify-between">
                        <span :class="[oaStyle.text.primary, 'text-sm']">Hiển thị vị trí bản đồ</span>
                        <input type="checkbox" v-model="form.showMap"
                            class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
                    </div>
                </div>
            </div>

            <!-- Accordion Box: Thông tin doanh nghiệp -->
            <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'border rounded-lg shadow-sm mb-4']">
                <div @click="toggleSection('business')"
                    :class="[sections.business ? 'border-b' : '', oaStyle.border.secondary, 'px-6 py-4 flex justify-between items-center cursor-pointer']">
                    <div :class="[oaStyle.text.primary, 'text-md font-medium']">Thông tin doanh nghiệp</div>
                    <i
                        :class="[oaStyle.text.secondary, sections.business ? 'fas fa-chevron-up text-xs' : 'fas fa-chevron-down text-xs']"></i>
                </div>
                <div v-show="sections.business" class="p-6 space-y-4">
                    <div class="flex items-center justify-between">
                        <span :class="[oaStyle.text.primary, 'text-sm w-40']">Địa chỉ</span>
                        <input type="text" v-model="form.address"
                            :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'flex-1 px-3 py-2 border rounded text-sm focus:outline-none']" />
                    </div>
                    <div class="flex items-center justify-between">
                        <span :class="[oaStyle.text.primary, 'text-sm w-40']">Số điện thoại</span>
                        <input type="text" v-model="form.phone"
                            :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'flex-1 px-3 py-2 border rounded text-sm focus:outline-none']" />
                    </div>
                    <div class="flex items-center justify-between">
                        <span :class="[oaStyle.text.primary, 'text-sm w-40']">Giờ hoạt động</span>
                        <input type="text" v-model="form.workingHours"
                            :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'flex-1 px-3 py-2 border rounded text-sm focus:outline-none']" />
                    </div>
                </div>
            </div>
        </main>

        <!-- RIGHT: MÁY ẢO PREVIEW (Mô phỏng điện thoại) -->
        <aside :class="[oaStyle.bg.primary, 'w-95 p-6 flex flex-col items-center justify-center overflow-y-auto']">
            <!-- Khung điện thoại -->
            <div
                :class="[oaStyle.bg.primary, oaStyle.border.secondary, 'w-75 h-145 rounded-[40px] border-6 shadow-xl overflow-hidden flex flex-col relative']">

                <!-- Status Bar điện thoại giả lập -->
                <div
                    :class="[oaStyle.text.primary, oaStyle.bg.secondary, 'px-4 py-1 text-[10px] flex justify-between items-center']">
                    <span>10:07</span>
                    <div class="flex space-x-1.5 items-center">
                        <i class="fas fa-signal text-[8px]"></i>
                        <i class="fas fa-wifi text-[8px]"></i>
                        <i class="fas fa-battery-full text-[8px]"></i>
                    </div>
                </div>

                <!-- Header App mô phỏng Zalo -->
                <div
                    :class="[oaStyle.text.primary, oaStyle.bg.secondary, 'px-3 py-2 flex items-center justify-between text-xs font-medium']">
                    <div class="flex items-center space-x-2">
                        <i class="fas fa-chevron-left"></i>
                        <span class="truncate max-w-45">{{ form.name }}</span>
                    </div>
                    <div class="flex space-x-3">
                        <i class="fas fa-ellipsis-h"></i>
                    </div>
                </div>

                <!-- Màn hình nội dung bên trong máy ảo -->
                <div :class="[oaStyle.bg.primary, 'flex-1 flex flex-col overflow-y-auto text-xs']"
                    style="scrollbar-width: none;">
                    <!-- Ảnh bìa hiển thị real-time -->
                    <div class="relative h-28">
                        <img :src="form.coverImage" alt="Cover Preview" class="w-full h-full object-cover" />
                    </div>

                    <!-- Thông tin profile OA -->
                    <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'px-4 pb-4 border-b relative']">
                        <!-- Ảnh đại diện chồng lên ảnh bìa -->
                        <div class="absolute -top-6 left-4">
                            <img :src="form.avatar" alt="Avatar Preview"
                                :class="[oaStyle.border.secondary, 'w-12 h-12 rounded-full border object-cover shadow-sm']" />
                        </div>

                        <div class="pt-8">
                            <div class="flex items-center space-x-1">
                                <span :class="[oaStyle.text.primary, 'font-medium text-sm']">{{ form.name }}</span>
                                <i class="fas fa-check-circle text-blue-500 text-[10px]"></i>
                            </div>
                            <p :class="[oaStyle.text.secondary, 'text-[10px] mt-0.5']">Doanh nghiệp</p>

                            <!-- Nút Quan tâm & Menu -->
                            <div class="flex items-center space-x-2 mt-3">
                                <button
                                    class="flex-1 bg-blue-600 text-white py-1.5 rounded-full font-medium text-center shadow-sm">
                                    Quan tâm
                                </button>
                                <button
                                    :class="[oaStyle.border.secondary, 'w-8 h-8 rounded-full border flex items-center justify-center text-gray-600']">
                                    <i class="fas fa-ellipsis"></i>
                                </button>
                            </div>

                            <div :class="[oaStyle.text.secondary, 'mt-2 text-[10px] flex items-center space-x-1']">
                                <i class="fas fa-shield-alt"></i>
                                <span>OA này đã được xác thực bởi Zalo</span>
                            </div>
                        </div>
                    </div>

                    <!-- Phần giới thiệu real-time -->
                    <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'p-3 border-b flex-1']">
                        <div :class="[oaStyle.text.primary, 'font-medium text-md mb-1']">Thông tin chi tiết</div>
                        <p :class="[oaStyle.text.secondary, 'leading-relaxed whitespace-pre-line']">{{ form.description
                            }}</p>

                        <div :class="[oaStyle.text.secondary, 'mt-3 space-y-1.5']">
                            <div v-if="form.showMap" class="flex items-start space-x-2">
                                <i class="fas fa-map-marker-alt mt-0.5"></i>
                                <span>{{ form.address }}</span>
                            </div>
                            <div v-if="form.showCallButton" class="flex items-center space-x-2">
                                <i class="fas fa-phone"></i>
                                <span>{{ form.phone }}</span>
                            </div>
                            <div class="flex items-center space-x-2">
                                <i class="fas fa-clock"></i>
                                <span>{{ form.workingHours }}</span>
                            </div>
                        </div>
                    </div>

                </div>

            </div>
        </aside>
    </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import { reactive } from 'vue';

// Trạng thái mở/đóng các accordion box
const sections = reactive({
    account: true,
    display: false,
    business: false
});

// Hàm chuyển đổi trạng thái mở rộng/thu gọn
const toggleSection = (key: 'account' | 'display' | 'business') => {
    sections[key] = !sections[key];
};

// Khai báo state liên kết dữ liệu 2 chiều từ Form sang máy ảo Preview
const form = reactive({
    name: 'Abaha Global',
    avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=60',
    coverImage: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=300&auto=format&fit=crop&q=60',
    description: 'Abaha là nền tảng thiết kế Mobile & Zalo Mini App thương mại điện tử. Abaha đã đóng gói trọn vẹn giải pháp cho các mô hình, bạn chỉ cần chọn mô hình phù hợp, chúng tôi triển khai trong 15 ngày.',
    address: '144 Khuất Duy Tiến, Nhân Chính, Thanh Xuân, Hà Nội',
    phone: '0927217227',
    workingHours: 'Hoạt động 24/24',
    showCallButton: true,
    showMap: true
});
</script>