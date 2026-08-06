<template>
    <div :class="[oaStyle.bg.primary, 'flex h-full']">
        <!-- LEFT: FORM CẤU HÌNH -->
        <main :class="[oaStyle.border.secondary, 'flex-1 overflow-y-auto p-8 border-r']">
            <!-- Page Header -->
            <div class="mb-6">
                <div :class="[oaStyle.text.primary, 'text-xl font-medium']">{{ t('oaInfo') }}</div>
                <p :class="[oaStyle.text.secondary, 'text-xs mt-0.5']">
                    {{ t('Cài đặt thông tin hiển thị để khuyến khích người dùng tương tác và quan tâm OA nhiều hơn.') }}
                </p>
            </div>

            <Collapse v-model:isOpen="sections.buttons" :title="t('button')" custom-class="p-6 space-y-6">

            </Collapse>

            <!-- Accordion Box: Thông tin tài khoản -->
            <Collapse v-model:isOpen="sections.oaInfo" :title="t('accountInfo')" custom-class="p-6 space-y-6">
                <AccountInfo v-model:oa="form"/>
            </Collapse>

            <!-- Accordion Box: Thiết lập hiển thị -->
            <Collapse v-model:isOpen="sections.interact" :title="t('interact')" custom-class="p-6 space-y-6">
                <div class="flex items-center justify-between">
                    <span :class="[oaStyle.text.primary, 'text-sm']">Hiển thị nút gọi nhanh</span>
                    <input type="checkbox" v-model="form.display.showCallButton"
                        class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
                </div>
                <div class="flex items-center justify-between">
                    <span :class="[oaStyle.text.primary, 'text-sm']">Hiển thị vị trí bản đồ</span>
                    <input type="checkbox" v-model="form.display.showAddress"
                        class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
                </div>
            </Collapse>

            <!-- Accordion Box: Thông tin doanh nghiệp -->
            <Collapse v-model:isOpen="sections.branchs" :title="t('businessSetting')" custom-class="p-6 space-y-6">
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
                    <input type="text" v-model="form.display.showWorkingHours"
                        :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'flex-1 px-3 py-2 border rounded text-sm focus:outline-none']" />
                </div>
            </Collapse>

            <Collapse v-model:isOpen="sections.remarkable" :title="t('remarkable')" custom-class="p-6 space-y-6">

            </Collapse>

            <Collapse v-model:isOpen="sections.articleResources" :title="t('articleResources')"
                custom-class="p-6 space-y-6">

            </Collapse>

            <Collapse v-model:isOpen="sections.utilities" :title="t('utilities')" custom-class="p-6 space-y-6">

            </Collapse>

            <!-- Nút Chỉnh sửa -->
            <div class="flex justify-end pt-2">
                <button
                    class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-medium rounded shadow transition-colors">
                    {{ t('update') }}
                </button>
            </div>
        </main>

        <!-- RIGHT: MÁY ẢO PREVIEW (Mô phỏng điện thoại) -->
        <VirtualMobile>
            <OaInforMobile :oa="form" />
        </VirtualMobile>
    </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import Collapse from '@/components/OA/Collapse/Collapse.vue';
import OaInforMobile from '@/components/Shared/VirtualMobile/OaInforMobile.vue';
import VirtualMobile from '@/components/Shared/VirtualMobile/VirtualMobile.vue';
import { useTranslate } from '@/composables/useTranslate';
import { OaType } from '@/types/entities';
import { computed, reactive, ref, watch } from 'vue';
import AccountInfo from './component/AccountInfo.vue';
import { useOaStore } from '@/stores/Oa/oa.storage.ts';

const oaStor = useOaStore()
// Trạng thái mở/đóng các accordion box
const sections = reactive({
    buttons: false,
    oaInfo: false,
    interact: false,
    branchs: false,
    remarkable: false,
    articleResources: false,
    utilities: false
});

const newOa = computed<OaType>(() => ({
    ...form,
}));

// Khai báo state liên kết dữ liệu 2 chiều từ Form sang máy ảo Preview
const form = reactive<OaType>({
  display: {
    showDescription: false,
    showAddress: false,
    showPhone: false,
    showWebsite: false,
    showWorkingHours: false,
    showCallButton: false,
  },
} as OaType)

const { t } = useTranslate()

watch(
  () => oaStor.oa,
  (oa) => {
    if (oa) Object.assign(form, oa)
  },
  { immediate: true }
)
</script>