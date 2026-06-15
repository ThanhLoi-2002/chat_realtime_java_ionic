<template>
    <div :class="[oaStyle.text.primary]">
        <OAPageHeader :breadcrumbs="myCrumbs" @toggle-sidebar="handleToggle"></OAPageHeader>
        Home
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import OAPageHeader from '@/components/OA/Header/OAPageHeader.vue';
import { ref } from 'vue';

const myCrumbs = ref([
  { name: 'Kịch bản tin nhắn' },
  { name: 'Tin nhắn chào mừng' },
  { name: 'Chỉnh sửa' }
]);

const handleToggle = () => {
  console.log('Bấm toggle menu');
};
</script>