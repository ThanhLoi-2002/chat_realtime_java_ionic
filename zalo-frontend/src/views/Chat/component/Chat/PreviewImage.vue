<template>
  <div class="fixed inset-0 z-50 flex flex-col bg-black/95 select-none" @click.self="closePreview">

    <div class="absolute top-0 left-0 right-0 h-12 flex items-center justify-end px-4 z-50">
      <button @click="closePreview"
        class="w-9 h-9 flex items-center justify-center cursor-pointer bg-white/10 hover:bg-white/20 rounded-full text-white transition-all">
        <i class="fa fa-close text-md" />
      </button>
    </div>

    <div class="flex flex-1 overflow-hidden mt-12">

      <div class="relative w-full">
        <div class="flex flex-col gap-2 absolute top-1/2 right-0">
          <button v-if="currentIndex > 0" @click="navigateImage(-1)"
            class="z-60 w-10 h-10 flex items-center justify-center rounded-full bg-slate-800 hover:bg-slate-700 text-white transition-all group cursor-pointer">
            <i class="fa fa-chevron-up text-2xl group-hover:scale-110" />
          </button>

          <button v-if="currentIndex < imageList.length - 1" @click="navigateImage(1)"
            class="z-60 w-10 h-10 flex items-center justify-center rounded-full bg-slate-800 hover:bg-slate-700 text-white transition-all group cursor-pointer">
            <i class="fa fa-chevron-down text-2xl group-hover:scale-110" />
          </button>
        </div>

        <div v-if="isVideo(messStorage.previewImage!.secureUrl)" class="relative h-full w-full">
          <video :src="messStorage.previewImage!.secureUrl" class="w-full h-full object-contain bg-black" controls
            autoplay></video>
        </div>

        <div v-else class="flex-1 flex items-center justify-center p-6 relative overflow-hidden h-full w-full"
          @click.stop @wheel.prevent="handleWheel">
          <img :src="messStorage.previewImage?.secureUrl" @dblclick="handleDoubleClick"
            class="max-w-[90%] max-h-[90%] object-contain shadow-2xl transition-transform duration-200 ease-out cursor-zoom-in"
            :style="{
              transform: `scale(${scale})`,
              transformOrigin: transformOrigin
            }" />

          <div class="absolute right-2 md:right-4 z-50 space-y-2">
          </div>

          <div
            class="absolute top-0 left-1/2 -translate-x-1/2 flex items-center gap-4 px-4 py-2 bg-black/50 backdrop-blur-md rounded-full border border-white/10 z-50">
            <button @click="zoom(-0.5)" class="text-white hover:text-blue-400 p-1 transition-colors cursor-pointer">
              <i class="fa fa-minus-circle text-lg" />
            </button>

            <span class="text-white text-xs font-mono min-w-10 text-center">
              {{ Math.round(scale * 100) }}%
            </span>

            <button @click="zoom(0.5)" class="text-white hover:text-blue-400 p-1 transition-colors cursor-pointer">
              <i class="fa fa-plus-circle text-lg" />
            </button>

            <div class="w-px h-4 bg-white/20 mx-1"></div>

            <button @click="scale = 1" class="text-white hover:text-red-400 p-1 transition-colors cursor-pointer"
              title="Reset">
              <i class="fa fa-undo text-sm" />
            </button>
          </div>
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

              <div v-if="isVideo(img.secureUrl)" class="relative h-full w-full">
                <img :src="img.secureUrl.replace(/\.[^/.]+$/, '.jpg')" class="w-full h-full object-cover opacity-80" />
                <div class="absolute inset-0 flex items-center justify-center">
                  <i class="fa-solid fa-circle-play text-white text-3xl shadow-lg"></i>
                </div>
              </div>

              <img v-else :src="img.secureUrl" class="w-full h-full object-cover" loading="lazy" />
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
import { MediaType } from "@/types/entities";
import { computed, nextTick, onMounted, ref, watch } from "vue";
import { useScroll } from "@/composables/useScroll";
import { MessageFilter } from "@/types/common";
import { MessageEnum } from "@/types/enum";
import { useTranslate } from "@/composables/useTranslate";
import { useMedia } from "@/composables/useMedia";
import { appLimit } from "@/utils/constant";

const { t } = useTranslate()
const scroll = ref<HTMLElement | null>(null);
const isCollapse = ref(true);
const { isVideo } = useMedia()

const messStorage = useMessageStore();
const convStorage = useConversationStore();
const { formatSeparatorTime } = useDateTime();
const { onScroll } = useScroll();

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
  if (e.key === "ArrowUp") navigateImage(-1);
  if (e.key === "ArrowDown") navigateImage(1);
};

const handleScroll = () => {
  // Chỉ gọi load more khi gần đầu trang và còn dữ liệu
  if (messStorage.imagesHasMore && convStorage.conversation) {
    const options: MessageFilter = {
      conversationId: convStorage.conversation.id,
      limit: appLimit.imageVideos,
      lastId: messStorage.images.at(-1)?.moduleId ?? undefined,
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

// --- THÊM VÀO PHẦN SCRIPT SETUP ---
const scale = ref(1);
const transformOrigin = ref('center center');

// Reset zoom khi đổi ảnh
watch(() => messStorage.previewImage, () => {
  scale.value = 1;
  transformOrigin.value = 'center center';
});

// Hàm điều chỉnh zoom thủ công
const zoom = (delta: number) => {
  const newScale = scale.value + delta;
  if (newScale >= 1 && newScale <= 5) {
    scale.value = newScale;
  }
};

// Zoom theo vị trí chuột khi Scroll
const handleWheel = (e: WheelEvent) => {
  if (e.ctrlKey || e.metaKey) return; // Cho phép zoom mặc định của trình duyệt nếu cần

  const zoomStep = 0.2;
  const delta = e.deltaY > 0 ? -zoomStep : zoomStep;
  const newScale = Math.min(Math.max(scale.value + delta, 1), 5);

  if (newScale !== scale.value) {
    // Tính toán tâm zoom dựa trên vị trí chuột
    const rect = (e.currentTarget as HTMLElement).getBoundingClientRect();
    const x = ((e.clientX - rect.left) / rect.width) * 100;
    const y = ((e.clientY - rect.top) / rect.height) * 100;

    transformOrigin.value = `${x}% ${y}%`;
    scale.value = newScale;
  }
};

// Double click để toggle zoom nhanh
const handleDoubleClick = (e: MouseEvent) => {
  if (scale.value > 1) {
    scale.value = 1;
  } else {
    const rect = (e.currentTarget as HTMLElement).getBoundingClientRect();
    const x = ((e.clientX - rect.left) / rect.width) * 100;
    const y = ((e.clientY - rect.top) / rect.height) * 100;
    transformOrigin.value = `${x}% ${y}%`;
    scale.value = 2.5;
  }
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
</script>
