<template>
    <div :class="[oaStyle.text.primary]">
        <OAPageHeader :breadcrumbs="myCrumbs" @toggle-sidebar="handleToggle"></OAPageHeader>

        <div class="px-2 py-4 space-y-2">
            <div class="grid grid-cols-12 gap-2 items-center">
                <span class="col-span-3 text-sm font-medium truncate">{{ t('language') }}</span>
                <div class="col-span-9">
                    <LangDropdown />
                </div>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import LangDropdown from '@/components/Dropdown/LangDropdown.vue';
import OAPageHeader from '@/components/OA/Header/OAPageHeader.vue';
import { useTranslate } from '@/composables/useTranslate';
import { ref } from 'vue';

const { t } = useTranslate()
const myCrumbs = ref([
    { name: 'Kịch bản tin nhắn' },
    { name: 'Tin nhắn chào mừng' },
    { name: 'Chỉnh sửa' }
]);

const handleToggle = () => {
    console.log('Bấm toggle menu');
};
</script>