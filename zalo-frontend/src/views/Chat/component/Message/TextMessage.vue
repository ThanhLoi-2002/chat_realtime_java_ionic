<template>
  <div
    ref="el"
    :class="[
      'text-sm flex flex-col relative min-w-12',
      'px-2 md:px-4 border rounded-lg',
      isOwner
        ? 'bg-blue-400 text-white border-blue-500'
        : 'bg-white dark:bg-gray-800 dark:text-slate-100 border-slate-300 dark:border-gray-700',
      message.reactions?.length > 0 ? 'pt-2 pb-4' : 'py-0.5 md:py-2',
      role && role != MemberRoleEnum.MEMBER ? 'border border-blue-900' : ''
    ]"
  >
    <!-- USERNAME -->
    <span
      v-if="message.isFirst && !isOwner"
      class="text-xs text-slate-400 dark:text-slate-300"
    >
      {{ message.sender?.username }}
    </span>

    <!-- CONTENT -->
    <div :class="[message.showTime ? 'py-1' : 'py-0.5']">
      <!-- nếu bị thu hồi -->
      <span
        v-if="message.stt === -1"
        class="italic text-gray-700 dark:text-gray-600"
      >
        {{ isOwner ? t("youRecalledmessage") : t("messageHasBeenWithdrawn") }}
      </span>

      <!-- text bình thường -->
      <span v-else>
        {{ message.content }}
      </span>
    </div>

    <!-- TIME -->
    <span
      v-if="message.showTime"
      :class="['text-[10px] md:text-xs', style.text.secondary]"
    >
      {{ formatTime(message.ct) }}
    </span>

    <Reactions :message="message"/>
  </div>
</template>
<script setup lang="ts">
import { style } from "@/assets/tailwindcss";
import { useDateTime } from "@/composables/useDateTime";
import { useTranslate } from "@/composables/useTranslate";
import { ref, onMounted, onBeforeUnmount } from "vue";
import Reactions from "../Reaction/Reactions.vue";
import { MemberRoleEnum } from "@/types/enum";

const props = defineProps<{
    setBubbleRef?: (el: HTMLElement | null) => void;
    message: any;
    isOwner: boolean;
    role?: MemberRoleEnum;
}>();

const { formatTime } = useDateTime();
const { t } = useTranslate();

const el = ref<HTMLElement | null>(null);

onMounted(() => {
    props.setBubbleRef?.(el.value ?? null);
});
onBeforeUnmount(() => {
    props.setBubbleRef?.(null);
});
</script>
