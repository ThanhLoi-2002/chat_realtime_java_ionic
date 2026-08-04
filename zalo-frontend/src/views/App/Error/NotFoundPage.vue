<template>
  <div class="h-full bg-slate-100 flex flex-col items-center justify-center p-6 text-center">
    <h1 class="text-9xl font-bold text-blue-600">404</h1>
    <p class="text-2xl font-semibold mt-4 text-slate-800">Ối! Trang không tồn tại</p>
    <p class="text-slate-500 mt-2 mb-8">
      Đường dẫn bạn truy cập không đúng hoặc đã bị gỡ bỏ.
    </p>

    <button @click="goBack"
      class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-full transition-all shadow-lg">
      {{ t('backHome') }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { goBack } from '@/utils/helper';

const { t } = useTranslate()
</script>