<template>
  <div ref="el" :class="[
    'text-sm flex flex-col relative min-w-12 px-2 md:px-4 border rounded-lg',
    'px-2 py-2',

    // Logic cho Background và Text
    isOwner ? 'bg-blue-900 text-white' : 'bg-white dark:bg-gray-800 dark:text-slate-100',

    // Logic Padding dựa trên reaction
    message.reactions?.length > 0 ? 'pt-2 pb-4' : 'py-0.5 md:py-2',

    // Logic riêng cho Border (Quan trọng)
    (role && role != MemberRoleEnum.MEMBER)
      ? 'border-blue-900 border' // Nếu có role đặc biệt
      : (isOwner ? 'border-blue-900' : 'border-slate-300 dark:border-gray-700') // Border mặc định
  ]" :data-id="message.id">
    <!-- USERNAME -->
    <span v-if="message.isFirst && !isOwner" class="text-xs text-slate-400 dark:text-slate-300">
      {{ message.sender?.username }}
    </span>

    <!-- CONTENT -->
    <div :class="[message.showTime ? 'py-1' : 'py-0.5']">
      <span v-if="message.stt === -1" class="italic text-gray-700 dark:text-gray-400">
        {{ isOwner ? t("youRecalledmessage") : t("messageHasBeenWithdrawn") }}
      </span>

      <div v-else class="flex flex-col gap-2">
        <span :class="isOwner ? 'text-white' : 'text-slate-800 dark:text-slate-100'" v-html="formattedContent"
          @click="handleContentClick">
        </span>
      </div>
    </div>

    <!-- TIME -->
    <span v-if="message.showTime" :class="['text-[10px] md:text-xs text-slate-500 dark:text-slate-400']">
      {{ formatTime(message.ct) }}
    </span>

    <Reactions :message="message" />

    <Modal ref="profileModal" :title="t('profile')">
      <FriendProfileUI :user="selectedUser" />
    </Modal>
  </div>
</template>
<script setup lang="ts">
import { style } from "@/assets/tailwindcss";
import { useDateTime } from "@/composables/useDateTime";
import { useTranslate } from "@/composables/useTranslate";
import { ref, onMounted, onBeforeUnmount, computed, nextTick } from "vue";
import Reactions from "../Reaction/Reactions.vue";
import { MemberRoleEnum } from "@/types/enum";
import Modal from "@/components/Modal/Modal.vue";
import FriendProfileUI from "../FriendProfileUI.vue";
import { MemberType, UserType } from "@/types/entities";
import { useConversationStore } from "@/stores/conversation.storage";
import { toast } from "@/utils/toast";
import { useMessage } from "@/composables/useMessage";

const props = defineProps<{
  setBubbleRef?: (el: HTMLElement | null) => void;
  message: any;
  isOwner: boolean;
  role?: MemberRoleEnum;
}>();

const { formatTime } = useDateTime();
const { t } = useTranslate();
const convStorage = useConversationStore()
const profileModal = ref()
const emit = defineEmits(['visible']);
const { formattedContentWithTag } = useMessage()
let observer: IntersectionObserver | null = null;

const el = ref<HTMLElement | null>(null);
const selectedUser = ref<UserType | undefined>(undefined)

const formattedContent = computed(() =>
  formattedContentWithTag(props.message.content, props.isOwner)
);

const handleContentClick = (event: MouseEvent) => {
  const target = event.target as HTMLElement;

  // Kiểm tra xem phần tử bị click có class 'mention-link' hay không
  if (target.classList.contains('mention-link')) {
    const userId = target.getAttribute('data-user-id');

    // Tìm thông tin user trong list members của cuộc trò chuyện
    const foundMember = convStorage.conversation?.members?.find(
      (m: MemberType) => m.id.toString() === userId
    );

    if (foundMember) {
      selectedUser.value = foundMember;
      // Mở modal thông tin cá nhân
      nextTick(() => {
        profileModal.value?.present();
      });
    } else {
      console.warn("Không tìm thấy user với ID:", userId);
      toast({
        message: t("notFound"),
        color: "danger"
      })
    }
  }
};

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
