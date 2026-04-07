<template>
  <div
    v-if="role && role != MemberRoleEnum.MEMBER"
    class="absolute w-4 h-4 rounded-full bg-black/60 flex items-center justify-center bottom-0 right-0"
  >
    <i
      v-if="role === MemberRoleEnum.ADMIN"
      class="fa fa-key text-yellow-500 text-[10px]"
      :title="t('admin')"
    ></i>

    <i
      v-else-if="role === MemberRoleEnum.SILVER_KEY"
      class="fa fa-key text-slate-400 text-[10px]"
      :title="t('deputy')"
    ></i>
  </div>
</template>
<script setup lang="ts">
import { useTranslate } from "@/composables/useTranslate";
import { UserType } from "@/types/entities";
import { MemberRoleEnum } from '@/types/enum'
import { style } from '@/assets/tailwindcss'

const props = defineProps < {
    role?: MemberRoleEnum;
} > ();
const { t } = useTranslate();
</script>
