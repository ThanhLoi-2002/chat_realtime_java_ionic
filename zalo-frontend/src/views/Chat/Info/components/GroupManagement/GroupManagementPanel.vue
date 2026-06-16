<template>
    <div class="h-full flex flex-col overflow-auto select-none">
        <div class="p-4 flex items-center gap-3 border-b border-slate-300 dark:border-slate-700"
            :class="[style.text.secondary]">
            <i class="fa fa-arrow-left cursor-pointer" @click="$emit('back')"></i>
            <span class="font-semibold m-auto">{{ t("groupManagement") }}</span>
        </div>

        <div v-if="!isAdmin()" class="bg-gray-100 dark:bg-gray-800 py-2.5 px-4 flex items-center justify-center gap-2 text-xs font-medium border-b border-slate-200 dark:border-slate-700 text-gray-500 dark:text-gray-400">
            <i class="fas fa-lock"></i>
            <span>{{ t("adminOnlyFeature") }}</span> </div>

        <div v-if="settingsOpt" 
            class="max-w-2xl mx-auto w-full p-3 flex flex-col gap-4 transition-all"
            :class="[!isAdmin() ? 'pointer-events-none opacity-60' : '']">

            <div class="overflow-hidden">
                <span class="py-2 text-slate-500 dark:text-slate-400 block font-medium text-sm">
                    {{ t("allowMembersTo") }}:
                </span>

                <div class="flex flex-col">
                    <div v-for="(opt, index) in memberOptions" :key="index" @click="toggleSetting(opt.key)"
                        class="py-2 flex justify-between items-center cursor-pointer">
                        <span class="text-sm text-wrap flex-1" :class="[style.text.secondary]">
                            {{ capitalize(t(opt.labelKey)) }}
                        </span>
                        <div class="relative flex items-center">
                            <div class="w-5 h-5 border rounded-md transition-all flex items-center justify-center"
                                :class="[
                                    settingsOpt[opt.key] 
                                        ? (isAdmin() ? 'bg-blue-600 border-blue-600' : 'bg-blue-500/50 border-blue-500/30') 
                                        : 'border-slate-300 dark:border-slate-600'
                                ]">
                                <i v-if="settingsOpt[opt.key]" class="fas fa-check text-white text-xs"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="overflow-hidden pt-2 border-t" :class="[style.border.primary]">
                <div class="flex flex-col">
                    <div v-for="(item, index) in adminSettings" :key="index" @click="toggleSetting(item.key)"
                        class="py-2 flex justify-between items-start gap-4 cursor-pointer">

                        <div class="flex-1 flex flex-col gap-1.5">
                            <div class="flex items-center gap-2">
                                <span class="text-sm text-wrap flex-1" :class="[style.text.secondary]">
                                    {{ capitalize(t(item.labelKey)) }}
                                </span>
                            </div>
                        </div>

                        <div class="mt-1 pointer-events-none">
                            <ion-toggle color="primary" 
                                :checked="!!settingsOpt[item.key]"
                                :disabled="!isAdmin()"
                                class="custom-toggle"></ion-toggle>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<script setup lang="ts">
import { nextTick, ref, watch } from 'vue';
import { style } from '@/assets/tailwindcss';
import { useTranslate } from '@/composables/useTranslate';
import { IonToggle } from '@ionic/vue';
import { useConversationStore } from '@/stores/App/conversation.storage';
import { capitalize } from '@/utils/helper';
import { GroupSetting } from '@/types/common';
import { useConversation } from '@/composables/useConversation';

const { t } = useTranslate();
const convStorage = useConversationStore()
const settingsOpt = ref<GroupSetting>()
const { isAdmin } = useConversation()

interface UIItem {
    labelKey: string;
    key: keyof GroupSetting;
}

const memberOptions = ref<UIItem[]>([]);
const adminSettings = ref<UIItem[]>([]);
// 🌟 Chiếc cờ dùng để chặn lượt chạy tự động khi nạp dữ liệu
let isInitializing = false;

// 🌟 HÀM XỬ LÝ CLICK ĐỒNG BỘ 2 CHIỀU
const toggleSetting = (key: keyof GroupSetting) => {
    if (settingsOpt.value && key in settingsOpt.value) {
        // Cập nhật trực tiếp vào Object gốc, giao diện v-for qua settingsOpt[item.key] sẽ tự thay đổi theo
        (settingsOpt.value[key] as boolean) = !settingsOpt.value[key];
    }
};

// Watch 1: Theo dõi thay đổi cuộc hội thoại để nạp giao diện
watch(() => [convStorage.conversation?.id, convStorage.conversation?.settings], () => {
    if (convStorage.conversation?.settings) {
        // 🌟 BẬT CỜ: Đang trong quá trình khởi tạo dữ liệu phòng mới
        isInitializing = true;

        memberOptions.value = [];
        adminSettings.value = [];

        settingsOpt.value = { ...convStorage.conversation.settings };

        for (const key in settingsOpt.value) {
            const currentKey = key as keyof GroupSetting;
            if (key.startsWith('allow')) {
                memberOptions.value.push({ labelKey: key, key: currentKey });
            } else {
                adminSettings.value.push({ labelKey: key, key: currentKey });
            }
        }

        // 🌟 HẠ CỜ: Sau khi DOM cập nhật và đồng bộ xong dữ liệu cũ, mở khóa cho phép gọi API
        nextTick(() => {
            isInitializing = false;
        });
    }
}, { immediate: true });

// Watch 2: Theo dõi để gọi API lưu trữ
watch(settingsOpt, async (newSettings) => {
    // 🛡️ CHẶN LẠI: Nếu cờ đang bật (do Watch 1 kích hoạt nạp data), thoát ra ngay không gọi API
    if (isInitializing) return;

    if (newSettings) {
        console.log("Người dùng thực sự click thay đổi, tiến hành gọi API:", newSettings);
        await convStorage.saveSetting(newSettings);
    }
}, { deep: true });
</script>

<style scoped>
/* Tùy chỉnh modern Ionic Toggle */
.custom-toggle {
    /* Điều chỉnh kích thước tổng thể cho mảnh hơn */
    --track-width: 38px;
    --track-height: 20px;
    --handle-width: 16px;
    /* Núm vặn nhỏ lại */
    --handle-height: 16px;
    --handle-spacing: 2px;
    /* Khoảng cách handle đến mép track */
    --track-padding: 0;
    --handle-border-radius: 50%;

    /* Màu sắc */
    --track-background: #ccc;
    /* Màu khi tắt (mẫu xám) */
    --track-background-checked: #3b82f6;
    /* Màu xanh Zalo khi bật */
    --handle-background: #fff;
    /* Núm luôn trắng */
    --handle-background-checked: #fff;

    /* Hiệu ứng mượt mà */
    transition: all 0.2s ease-in-out;

    width: 38px;
    /* Khớp với track-width */
    height: 20px;
}

/* Sửa lỗi shadow thô của Ionic mặc định */
.custom-toggle::part(track) {
    box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
}

.custom-toggle::part(handle) {
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

/* Dark Mode */
.dark .custom-toggle {
    --track-background: #444;
    /* Màu track tối khi tắt */
}
</style>