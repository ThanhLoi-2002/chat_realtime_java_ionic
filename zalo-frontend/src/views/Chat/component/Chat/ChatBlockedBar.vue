<script setup lang="ts">
import { computed } from 'vue';
import { useTranslate } from '@/composables/useTranslate';
import { capitalize } from '@/utils/helper';

const props = defineProps<{
  isCommunity: boolean
}>();

const { t } = useTranslate();

// Tính toán chuỗi văn bản dựa theo loại nhóm
const blockedMessage = computed(() => {
  const roleText = props.isCommunity ? 'trưởng/phó cộng đồng' : 'quản trị viên';
  const groupText = props.isCommunity ? 'cộng đồng' : 'nhóm';
  return `Chỉ ${roleText} được gửi tin nhắn vào ${groupText}.`;
});
</script>

<template>
  <div class="w-full px-4 py-3 flex items-start gap-3 bg-slate-100 dark:bg-[#182232] border-t border-slate-200 dark:border-slate-800 select-none">
    
    <div class="shrink-0 mt-0.5 text-blue-500 dark:text-blue-400">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
        <path stroke-linecap="round" stroke-linejoin="round" d="M11.25 11.25l.041-.02a.75.75 0 111.083.984l-.04.016-1.055.421a.75.75 0 01-.62 0l-1.055-.421a.75.75 0 111.084-.984l.04.02m1.056-2.222h.008v.008h-.008V9.13zM21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
    </div>

    <div class="text-sm leading-relaxed text-slate-600 dark:text-slate-300 flex-1">
      <span>{{ capitalize(blockedMessage) }}</span>
      
      <a href="#" class="inline-block ml-1 text-blue-600 dark:text-blue-400 font-medium hover:underline cursor-pointer">
        {{ t("learnMore") }}
      </a>
    </div>

  </div>
</template>