<template>
  <div class="fixed inset-0 z-50 flex flex-col bg-black/95 select-none" @click.self="closePreview">

    <div class="absolute top-0 left-0 right-0 h-12 flex items-center justify-end px-4 z-50">
      <button @click="closePreview"
        class="w-9 h-9 flex items-center justify-center cursor-pointer bg-white/10 hover:bg-white/20 rounded-full text-white transition-all">
        <i class="fa fa-close text-md" />
      </button>
    </div>

    <div class="flex flex-1 overflow-hidden mt-12">

      <div class="flex-1 flex items-center justify-center p-6 relative" @click.stop>

        <img :src="messStorage.previewImage?.secureUrl"
          class="max-w-[90%] max-h-[90%] object-contain transition duration-300 shadow-2xl" />

        <div class="absolute right-4 z-50 space-y-2">
          <button v-if="currentIndex > 0" @click="navigateImage(-1)"
            class="w-12 h-12 flex items-center justify-center bg-white/10 hover:bg-white/20 rounded-full text-white transition-all backdrop-blur-sm cursor-pointer">
            <i class="fa fa-chevron-up text-xl" />
          </button>

          <button v-if="currentIndex < imageList.length - 1" @click="navigateImage(1)"
            class="w-12 h-12 flex items-center justify-center bg-white/10 hover:bg-white/20 rounded-full text-white transition-all backdrop-blur-sm cursor-pointer">
            <i class="fa fa-chevron-down text-xl" />
          </button>
        </div>

      </div>

      <div ref="scroll"
        class="hidden md:flex w-36 border-l border-white/5 bg-black/20 flex-col items-center py-4 gap-4 overflow-y-auto custom-scrollbar"
        @click.stop @scroll="handleScroll">
        <div v-for="(group, date) in groupedImages" :key="date" class="w-full flex flex-col items-center mb-4">
          <span class="text-xs text-gray-500 mb-2 font-bold">{{ date }}</span>
          <div class="flex flex-col gap-3">
            <div v-for="img in group" :key="img.id" :id="'thumb-' + img.id" @click="handlePreviewImage(img)"
              class="relative w-24 h-24 rounded-lg overflow-hidden cursor-pointer transition-all border-2"
              :class="img.id === messStorage.previewImage?.id ? 'border-blue-500 scale-105 shadow-lg' : 'border-transparent opacity-50 hover:opacity-100'">
              <img :src="img.secureUrl" class="w-full h-full object-cover" loading="lazy" />
              <div v-if="img.messageContent"
                class="absolute bottom-0 left-0 right-0 bg-black/60 text-[10px] text-white px-1 py-0.5 truncate">

                {{ img.messageContent }}

              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      class="relative h-20 w-full flex flex-col items-center justify-center border-t border-white/5 bg-black/40 backdrop-blur-md">
      <div v-if="messStorage.previewImage?.messageContent"
        class="absolute -top-4 -translate-y-full w-full max-w-xl px-4 pointer-events-auto">
        <collapse v-model:isOpen="isCollapse" :title="t('description')" class="bg-black/60 rounded-md py-2!">
          <div class="text-white">
            <p class="text-sm text-center truncate max-h-32 overflow-y-auto">
              {{ messStorage.previewImage?.messageContent }}
            </p>
          </div>
        </collapse>
      </div>

      <div class="flex items-center gap-2 w-full ml-10">
        <CircleAvatar :user="messStorage.previewImage?.createdBy" :isDisabled="true" />
        <div class="flex flex-col">
          <span class="text-slate-100 font-semibold text-sm">{{ messStorage.previewImage?.createdBy?.username }}</span>
          <span class="text-gray-400 text-xs">{{ formatSeparatorTime(messStorage.previewImage?.ct) }}</span>
        </div>
      </div>
    </div>

  </div>
</template>
<script setup lang="ts">
import CircleAvatar from "@/components/Avatar/CircleAvatar.vue";
import { useDateTime } from "@/composables/useDateTime";
import { useConversationStore } from "@/stores/conversation.storage";
import { useMessageStore } from "@/stores/message.storage";
import { MediaType, MessageType } from "@/types/entities";
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { style } from "@/assets/tailwindcss";
import { useScroll } from "@/composables/useScroll";
import { MessageFilter } from "@/types/common";
import { MessageEnum } from "@/types/enum";
import { useTranslate } from "@/composables/useTranslate";

const { t } = useTranslate()
const scroll = ref<HTMLElement | null>(null);
const isCollapse = ref(true);

const messStorage = useMessageStore();
const convStorage = useConversationStore();
const { formatSeparatorTime } = useDateTime();
const { scrollToBottom, onScroll } = useScroll();

const closePreview = () => {
  messStorage.setPreviewImage(undefined);
};

// Tính toán index hiện tại
const currentIndex = computed(() => {
  return imageList.value.findIndex(img => img.id === messStorage.previewImage?.id);
});

// Hàm chuyển ảnh
const navigateImage = (step: number) => {
  const newIndex = currentIndex.value + step;

  // Kiểm tra giới hạn danh sách
  if (newIndex >= 0 && newIndex < imageList.value.length) {
    const nextImg = imageList.value[newIndex];
    handlePreviewImage(nextImg);
  }
};

// Cập nhật handleKey để dùng phím mũi tên
const handleKey = (e: KeyboardEvent) => {
  if (e.key === "Escape") closePreview();
  if (e.key === "ArrowLeft") navigateImage(-1); // Phím mũi tên trái
  if (e.key === "ArrowRight") navigateImage(1); // Phím mũi tên phải
};

const handleScroll = () => {
  // Chỉ gọi load more khi gần đầu trang và còn dữ liệu
  if (messStorage.imagesHasMore && convStorage.conversation) {
    const options: MessageFilter = {
      conversationId: convStorage.conversation.id,
      limit: 20,
      lastId: messStorage.images.at(-1)?.id ?? undefined,
      contentType: MessageEnum.IMAGE,
    };
    onScroll(scroll.value, () => messStorage.getImageMessages(options));
  }
};

const groupedImages = computed(() => {
  const groups: Record<string, any[]> = {};

  // Sắp xếp đảo ngược để ảnh mới nhất ở dưới hoặc trên tùy ý bạn
  const list = [...messStorage.images].reverse();

  list.forEach((img) => {
    // Logic format date đơn giản (DD/MM)
    const date = formatSeparatorTime(img.ct, "onlyDay");

    if (!groups[date]) groups[date] = [];
    groups[date].push(img);
  });

  return groups;
});

const handlePreviewImage = (pi: MediaType) => {
  messStorage.setPreviewImage(pi);
};

const imageList = computed(() => [...messStorage.images].reverse());

// Thêm hàm cuộn vào script setup
const scrollToActiveThumb = (retries = 5) => {
  const activeId = messStorage.previewImage?.id;
  if (!activeId) return;

  nextTick(() => {
    const activeElement = document.getElementById(`thumb-${activeId}`);

    if (activeElement) {
      activeElement.scrollIntoView({
        behavior: 'smooth',
        block: 'center',
      });
    } else if (retries > 0) {
      // Nếu chưa thấy element (có thể do groupedImages đang render), thử lại sau 100ms
      setTimeout(() => scrollToActiveThumb(retries - 1), 100);
    }
  });
};


watch(() => messStorage.previewImage, async (newVal) => {
  isCollapse.value = true;
  if (newVal) {
    // Chờ 1: Chờ Vue cập nhật DOM (v-if/v-show chuyển thành true)
    await nextTick();

    // Chờ 2: Chờ thêm một khoảng nhỏ để trình duyệt hoàn tất việc render layout
    // Lần đầu mở thường tốn thời gian render nặng hơn
    setTimeout(() => {
      scrollToActiveThumb();
    }, 200);
  }
}, { immediate: true });

// Gọi khi lần đầu tiên mount (nếu đã có ảnh được chọn sẵn)
onMounted(() => {
  window.addEventListener("keydown", handleKey);
  if (messStorage.previewImage) {
    scrollToActiveThumb();
  }
});

// watch(() => scroll.value, () => {
//     scrollToBottom(scroll.value, true)
// })
</script>
