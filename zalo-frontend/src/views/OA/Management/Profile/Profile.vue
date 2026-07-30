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
                            <img :src="form.cover" alt="Cover"
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
        <VirtualMobile>
            <OaInforMobile :oa="oa" />
        </VirtualMobile>
    </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import OaInforMobile from '@/components/Shared/VirtualMobile/OaInforMobile.vue';
import VirtualMobile from '@/components/Shared/VirtualMobile/VirtualMobile.vue';
import { OaType } from '@/types/entities';
import { computed, reactive } from 'vue';

// Trạng thái mở/đóng các accordion box
const sections = reactive({
    account: true,
    display: false,
    business: false
});

const oa = computed<OaType>(() => ({
  ...form,
}));

// Hàm chuyển đổi trạng thái mở rộng/thu gọn
const toggleSection = (key: 'account' | 'display' | 'business') => {
    sections[key] = !sections[key];
};

// Khai báo state liên kết dữ liệu 2 chiều từ Form sang máy ảo Preview
const form = reactive<any>({
    name: 'Abaha Global',
    avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=60',
    cover: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=300&auto=format&fit=crop&q=60',
    description: 'Abaha là nền tảng thiết kế Mobile & Zalo Mini App thương mại điện tử. Abaha đã đóng gói trọn vẹn giải pháp cho các mô hình, bạn chỉ cần chọn mô hình phù hợp, chúng tôi triển khai trong 15 ngày.',
    address: '144 Khuất Duy Tiến, Nhân Chính, Thanh Xuân, Hà Nội',
    phone: '0927217227',
    workingHours: 'Hoạt động 24/24',
    showCallButton: true,
    showAddress: true
});
</script>