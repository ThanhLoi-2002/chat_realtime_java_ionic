<template>
    <div class="h-full flex flex-col overflow-auto">
        <div class="p-4 flex items-center gap-3 border-b border-slate-300 dark:border-slate-700"
            :class="[style.text.secondary]">
            <i class="fa fa-arrow-left cursor-pointer" @click="$emit('back')"></i>
            <span class="font-semibold m-auto">{{ t("groupManagement") }}</span>
        </div>

        <div class="max-w-2xl mx-auto w-full p-3 flex flex-col gap-4">

            <div class="overflow-hidden">
                <span class="py-2 text-blue-600 dark:text-blue-400">
                    {{ t("allowMembersTo") }}:
                </span>

                <div class="flex flex-col">
                    <div v-for="(opt, index) in memberOptions" :key="index" @click="opt.value = !opt.value"
                        class="py-2 flex justify-between items-center cursor-pointer">
                        <span class="text-sm truncate flex-1" :class="[style.text.secondary]">
                            {{ t(opt.labelKey) }}
                        </span>
                        <div class="relative flex items-center">
                            <div class="w-5 h-5 border rounded-md transition-all flex items-center justify-center"
                                :class="opt.value ? 'bg-blue-600 border-blue-600' : 'border-slate-300 dark:border-slate-600'">
                                <i v-if="opt.value" class="fas fa-check text-white text-xs"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="overflow-hidden pt-2 border-t" :class="[style.border.primary]">
                <div class="flex flex-col">
                    <div v-for="(item, index) in adminSettings" :key="index" @click="toggleAdminSetting(index)"
                        class="py-2 flex justify-between items-start gap-4 cursor-pointer">

                        <div class="flex-1 flex flex-col gap-1.5">
                            <div class="flex items-center gap-2">
                                <span class="text-sm truncate flex-1" :class="[style.text.secondary]">
                                    {{ t(item.labelKey) }}
                                </span>
                                <i v-if="item.hasHelp"
                                    class="far fa-question-circle text-slate-400 hover:text-blue-500 transition-colors text-xs"
                                    @click.stop="showHelp(item.labelKey)"></i>
                            </div>
                            <p v-if="item.subLabelKey"
                                class="text-xs text-slate-500 dark:text-slate-400 leading-relaxed pr-4">
                                {{ t(item.subLabelKey) }}
                            </p>
                        </div>

                        <div class="mt-1 pointer-events-none">
                            <ion-toggle color="primary" :checked="item.value" class="custom-toggle"></ion-toggle>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { style } from '@/assets/tailwindcss';
import { useTranslate } from '@/composables/useTranslate';
import { IonToggle } from '@ionic/vue';

const { t } = useTranslate();

const memberOptions = ref([
    { labelKey: 'changeGroupInfo', value: false },
    { labelKey: 'pinMessages', value: false },
    { labelKey: 'createNotes', value: false },
    { labelKey: 'createPoll', value: false },
    { labelKey: 'sendMessages', value: true },
]);

const adminSettings = ref([
    { labelKey: 'hideMemberList', subLabelKey: 'hideMemberListDesc', value: true, hasHelp: false },
    { labelKey: 'membershipApproval', value: true, hasHelp: true },
    { labelKey: 'highlightAdminMessages', value: true, hasHelp: true },
    { labelKey: 'newMembersReadRecentMessages', value: true, hasHelp: true }
]);

const toggleAdminSetting = (index: number) => {
    adminSettings.value[index].value = !adminSettings.value[index].value;
};

const showHelp = (key: string) => {
    console.log("Help for:", key);
};
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