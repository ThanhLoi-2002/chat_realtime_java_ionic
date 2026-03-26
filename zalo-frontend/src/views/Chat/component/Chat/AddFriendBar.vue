<template>
    <div class="absolute top-1 left-1/2 -translate-x-1/2
         px-4 py-2 flex gap-2 justify-between
         bg-gray-100 dark:bg-slate-700 border
         border-slate-300 dark:border-slate-800
         z-10 rounded-md shadow min-w-[98%]
         text-xs md:text-base" :class="[style.text.primary]">
            <div class="flex gap-2 items-center">
                <i class="fa-solid fa-user-plus"></i>
                <span :class="[style.text.primary, 'text-sm']">{{ t('sendFriendRequest') }}</span>
            </div>
            <div class="flex gap-4 items-center">
                <div class="px-2 py-1 text-sm rounded-sm cursor-pointer bg-slate-300 dark:bg-slate-600" :class="[style.text.primary]">{{ t("addFriend") }}</div>
                <i class="fa-solid fa-ellipsis cursor-pointer"></i>
            </div>
        </div>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import { useTranslate } from '@/composables/useTranslate';

const { t } = useTranslate()
</script>