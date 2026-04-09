<template>
  <div ref="el" :class="[
    'text-sm flex flex-col relative min-w-12 px-2 md:px-4 border rounded-lg',

    // Logic cho Background và Text
    isOwner ? 'bg-blue-400 text-white' : 'bg-white dark:bg-gray-800 dark:text-slate-100',

    // Logic Padding dựa trên reaction
    message.reactions?.length > 0 ? 'pt-2 pb-4' : 'py-0.5 md:py-2',

    // Logic riêng cho Border (Quan trọng)
    (role && role != MemberRoleEnum.MEMBER)
      ? 'border-blue-500 border' // Nếu có role đặc biệt
      : (isOwner ? 'border-blue-500' : 'border-slate-300 dark:border-gray-700') // Border mặc định
  ]" :data-id="message.id">
    <!-- USERNAME -->
    <span v-if="message.isFirst && !isOwner" class="text-xs text-slate-400 dark:text-slate-300">
      {{ message.sender?.username }}
    </span>

    <!-- CONTENT -->
    <div :class="[message.showTime ? 'py-1' : 'py-0.5']">
      <span v-if="message.stt === -1" class="italic text-gray-700 dark:text-gray-600">
        {{ isOwner ? t("youRecalledmessage") : t("messageHasBeenWithdrawn") }}
      </span>

      <div v-else class="flex flex-col gap-2">
        <div v-if="message.attachments?.length === 1" class="">
          <div class="max-w-62.5 md:max-w-87.5 rounded-lg overflow-hidden border border-black/5 shadow-sm">

            <img v-if="message.attachments[0].resourceType === ResourceEnum.IMAGE" :src="message.attachments[0].secureUrl"
              class="w-full h-auto object-cover cursor-pointer hover:opacity-95 transition-opacity"
              @click="messStorage.setPreviewImage(message)" />

            <video v-else-if="message.attachments[0].resourceType === ResourceEnum.VIDEO" :src="message.attachments[0].secureUrl"
              controls class="w-full h-auto" />
          </div>
        </div>

        <span v-if="message.content" :class="isOwner ? 'text-white' : 'text-slate-800 dark:text-slate-100'">
          {{ message.content }}
        </span>

      </div>
    </div>

    <!-- TIME -->
    <span v-if="message.showTime" :class="['text-[10px] md:text-xs', style.text.secondary]">
      {{ formatTime(message.ct) }}
    </span>

    <Reactions :message="message" />
  </div>
</template>
<script setup lang="ts">
import { style } from "@/assets/tailwindcss";
import { useDateTime } from "@/composables/useDateTime";
import { useTranslate } from "@/composables/useTranslate";
import { ref, onMounted, onBeforeUnmount } from "vue";
import Reactions from "../Reaction/Reactions.vue";
import { MemberRoleEnum, ResourceEnum } from "@/types/enum";
import { useMessageStore } from "@/stores/message.storage";

const props = defineProps<{
  setBubbleRef?: (el: HTMLElement | null) => void;
  message: any;
  isOwner: boolean;
  role?: MemberRoleEnum;
}>();

const { formatTime } = useDateTime();
const { t } = useTranslate();
const messStorage = useMessageStore()
const emit = defineEmits(['visible']);
let observer: IntersectionObserver | null = null;

const el = ref<HTMLElement | null>(null);

onMounted(() => {
  // Cấu hình: Khi tin nhắn hiện ra 50% diện tích thì tính là "đã xem"
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        emit('visible', props.message.id);
      }
    });
  }, { threshold: 0.5 });

  if (el.value) observer.observe(el.value);

  props.setBubbleRef?.(el.value ?? null);
});
onBeforeUnmount(() => {
  observer?.disconnect()
  props.setBubbleRef?.(null);
});
</script>
