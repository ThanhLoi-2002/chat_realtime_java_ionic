<template>
  <div ref="el" :class="[
    'text-sm flex flex-col relative min-w-12 px-2 md:px-3 border rounded-lg',
    'px-2 py-2',

    // Logic cho Background và Text
    isOwner ? 'bg-blue-400 dark:bg-blue-900 text-white' : 'bg-white dark:bg-gray-800 dark:text-slate-100',

    // Logic Padding dựa trên reaction
    message.reactions?.length > 0 ? 'pt-2 pb-4' : 'py-0.5 md:py-2',

    // Logic riêng cho Border (Quan trọng)
    (role && role != MemberRoleEnum.MEMBER)
      ? 'border-blue-400 dark:border-blue-900 border' // Nếu có role đặc biệt
      : (isOwner ? 'border-blue-400 dark:border-blue-900' : 'border-slate-300 dark:border-gray-700') // Border mặc định
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
        <ReplyingMessage v-if="message.replyToMessage" :replying-message="message.replyToMessage" />

        <span :class="isOwner ? 'text-white' : 'text-slate-800 dark:text-slate-100'" v-html="formattedContent"
          @click="handleContentClick">
        </span>

        <div v-if="linkPreview" @click="openLink(linkPreview.url)"
          class="mt-1 flex flex-col w-full max-w-full border rounded-xl overflow-hidden bg-white/10 dark:bg-gray-900/50 shadow-sm cursor-pointer hover:bg-black/5 transition">

          <div v-if="linkPreview.image" class="w-full h-32 md:h-40 overflow-hidden border-b border-black/5">
            <img :src="linkPreview.image" class="w-full h-full object-cover" />
          </div>

          <div class="p-2 min-w-0">
            <p class="font-bold text-[13px] line-clamp-1 text-inherit leading-tight">
              {{ linkPreview.title }}
            </p>
            <p v-if="linkPreview.description" class="text-[11px] opacity-80 line-clamp-2 mt-1">
              {{ linkPreview.description }}
            </p>
            <div class="flex items-center gap-1 mt-1.5">
              <i class="fa-solid fa-link text-[8px] opacity-70"></i>
              <span class="text-[10px] uppercase font-bold tracking-wider opacity-70">
                {{ formatHostname(linkPreview.url) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- TIME -->
    <span v-if="message.showTime" :class="['text-[10px] md:text-xs text-slate-300 dark:text-slate-400']">
      {{ formatTime(message.ct) }}
    </span>

    <Reactions :message="message" />

    <Modal ref="profileModal" :title="t('profile')">
      <FriendProfileUI :user="selectedUser" />
    </Modal>

    <Modal ref="groupInfoModal" :title="t('groupProfile')">
      <GroupProfile v-if="group" :conversation="group" :isMember="isMember" />
    </Modal>
  </div>
</template>
<script setup lang="ts">
import { useDateTime } from "@/composables/useDateTime";
import { useTranslate } from "@/composables/useTranslate";
import { ref, onMounted, onBeforeUnmount, computed, nextTick } from "vue";
import Reactions from "../Reaction/Reactions.vue";
import { MemberRoleEnum } from "@/types/enum";
import Modal from "@/components/Modal/Modal.vue";
import FriendProfileUI from "../FriendProfileUI.vue";
import { ConversationType, MemberType, UserType } from "@/types/entities";
import { useConversationStore } from "@/stores/conversation.storage";
import { toast } from "@/utils/toast";
import { useMessage } from "@/composables/useMessage";
import ReplyingMessage from "./ReplyingMessage.vue";
import { qrCodeUrl } from "@/utils/constant";
import GroupProfile from "../GroupProfile.vue";
import { useUserStore } from "@/stores/user.storage";

const props = defineProps<{
  setBubbleRef?: (el: HTMLElement | null) => void;
  message: any;
  isOwner: boolean;
  role?: MemberRoleEnum;
}>();

const { formatTime } = useDateTime();
const { t } = useTranslate();
const convStorage = useConversationStore()
const userStorage = useUserStore()
const profileModal = ref()
const groupInfoModal = ref()
const group = ref<ConversationType | undefined>(undefined)
const isMember = ref(false)
const emit = defineEmits(['visible']);
const { formattedContentWithTag, formatHostname } = useMessage()
let observer: IntersectionObserver | null = null;

const el = ref<HTMLElement | null>(null);
const selectedUser = ref<UserType | undefined>(undefined)

const formattedContent = computed(() => {
  // Lấy nội dung đã được xử lý tag từ composable của bạn
  const content = formattedContentWithTag(props.message.content, props.isOwner);

  // Regex để tìm URL (http, https, www)
  const urlRegex = /(https?:\/\/[^\s]+|www\.[^\s]+)/g;

  // Thay thế URL thành thẻ <a> hoặc <span> có class để handle click
  // Ở đây dùng class 'chat-url' để tô màu và handle click qua event delegation
  return content.replace(urlRegex, (url) => {
    const href = url.startsWith('http') ? url : `https://${url}`;
    return `<span class="text-blue-400 chat-url cursor-pointer underline break-all" data-url="${href}">${url}</span>`;
  });
}
);

const handleContentClick = async (event: MouseEvent) => {
  const target = event.target as HTMLElement;

  // XỬ LÝ CLICK URL (Mới)
  if (target.classList.contains('chat-url')) {
    const url = target.getAttribute('data-url');
    if (url) {
      // open group info popup
      if (url.startsWith(qrCodeUrl)) {
        const code = url.split('/').at(-1)
        if (code) {
          group.value = await convStorage.fetchGroupByCode(code)

          if (group.value) {
            isMember.value = group.value.members.some(u => u.id == userStorage.user?.id)
            groupInfoModal.value?.present()
            console.log(group.value)
          }

        }
      } else
        window.open(url, '_blank');
    }
    return; // Dừng lại không chạy logic mention bên dưới
  }

  // Kiểm tra xem phần tử bị click có class 'mention-link' hay không
  if (target.classList.contains('mention-link')) {
    const userId = target.getAttribute('data-user-id');

    // Tìm thông tin user trong list members của cuộc trò chuyện
    const foundMember = convStorage.conversation?.members?.find(
      (m: MemberType) => m.id.toString() == userId
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

// Thêm computed này vào phần script
const linkPreview = computed(() => {
  if (!props.message.linkMetadata) return null;

  // Nếu là string thì parse, nếu là object rồi thì dùng luôn
  try {
    return typeof props.message.linkMetadata === 'string'
      ? JSON.parse(props.message.linkMetadata)
      : props.message.linkMetadata;
  } catch (e) {
    console.error("Lỗi parse linkMetadata:", e);
    return null;
  }
});

// Hàm để mở link khi click vào card
const openLink = (url: string) => {
  if (url) window.open(url, '_blank');
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