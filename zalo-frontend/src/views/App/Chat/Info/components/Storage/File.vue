<template>
  <div class='w-full h-full'>
    <!-- LIST -->
    <div class="flex-1 overflow-y-auto px-3 pb-4 space-y-4 w-full h-[80%]" ref="scrollRef"
      @scroll="handleScroll">
      <div v-for="group in groupedMedia" :key="group.date">
        <!-- DATE -->
        <div class="text-gray-400 text-sm font-semibold mb-2">
          {{ group.date }}
        </div>

        <!-- ITEMS -->
        <div class="space-y-1 w-full">
          <FileContainer
            v-for="item in group.items"
            :key="item.id"
            class="flex items-center gap-3"
            :media="item"
            :isShowAction="true"
          />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useMedia } from "@/composables/useMedia";
import { useConversationStore } from "@/stores/App/conversation.storage.ts";
import { useMessageStore } from "@/stores/App/message.storage.ts";
import { MessageFilter } from "@/types/common";
import { MessageEnum } from "@/types/enum";
import { computed, onMounted, ref } from "vue";
import { appLimit } from "@/utils/constant";
import FileContainer from "./FileContainer.vue";

const convStorage = useConversationStore();
const messStorage = useMessageStore();
const { groupedMediaByTime } = useMedia();

const scrollRef = ref<HTMLElement | null>(null);

const groupedMedia = computed(() => {
  return groupedMediaByTime(filtered.value);
});

const filterDate = ref("");

const filtered = computed(() => {
  return messStorage.files.filter((item) => {
    // filter user
    // if (filterUser.value === 'me' && !item.isMe) return false
    // if (filterUser.value === 'other' && item.isMe) return false

    // filter date
    const now = new Date();
    const itemDate = new Date(item.ct);

    if (filterDate.value === "today") {
      if (itemDate.toDateString() !== now.toDateString()) return false;
    }

    if (filterDate.value === "week") {
      const diff = (now.getTime() - itemDate.getTime()) / (1000 * 60 * 60 * 24);
      if (diff > 7) return false;
    }

    if (filterDate.value === "month") {
      const diff = (now.getTime() - itemDate.getTime()) / (1000 * 60 * 60 * 24);
      if (diff > 30) return false;
    }

    return true;
  });
});

const fetchFileMessages = () => {
  if (!messStorage.filesHasMore) return;

  const lastId = messStorage.files.at(-1)?.moduleId ?? undefined;
  const options: MessageFilter = {
    conversationId: convStorage.conversation!.id,
    limit: appLimit.files,
    lastId,
    contentType: MessageEnum.FILE,
  };

  messStorage.getFileMessages(options);
};

// Xử lý sự kiện cuộn
const handleScroll = () => {
  // Vì đây là danh sách Media (dạng Grid), thường người dùng cuộn XUỐNG để xem ảnh cũ hơn
  // Bạn cần kiểm tra xem scroll container đã gần tới ĐÁY chưa
  const el = scrollRef.value;
  if (!el) return;

  // Logic: Nếu khoảng cách tới đáy < 150px thì tải thêm
  const isBottom = el.scrollHeight - el.scrollTop <= el.clientHeight + 150;
  if (isBottom) {
    fetchFileMessages();
  }
};

onMounted(() => {
  fetchFileMessages();
});
</script>
