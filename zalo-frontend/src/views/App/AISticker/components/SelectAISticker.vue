<template>
  <div class="w-full h-full flex flex-col bg-white font-sans text-slate-800 relative">

    <header class="flex items-center justify-between px-4 py-3 border-b border-gray-100 relative bg-white shrink-0">
      <div class="flex items-center gap-3">
        <button v-if="currentStep === 2" @click="currentStep = 1"
          class="p-1 hover:bg-gray-100 rounded-full transition text-gray-600">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5"
            stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5 3 12m0 0 7.5-7.5M3 12h18" />
          </svg>
        </button>
        <button v-else @click="isMenuOpen = !isMenuOpen"
          class="p-1 hover:bg-gray-100 rounded-full transition text-gray-700">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
            class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
          </svg>
        </button>
        <h1 class="text-lg font-semibold text-gray-800">{{ currentStep === 1 ? 'zSticker AI' : 'Tùy chỉnh Sticker' }}
        </h1>
      </div>
      <div
        class="flex items-center gap-2 bg-gray-50 border border-gray-200 rounded-full px-2.5 py-1 shrink-0 scale-90 origin-right">
        <span class="text-sm font-bold tracking-widest leading-none select-none text-gray-500">•••</span>
        <div class="w-px h-3 bg-gray-300 mx-0.5"></div>
        <button class="text-gray-500 hover:text-black">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
            class="w-4 h-4">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </header>

    <div v-if="currentStep === 1" class="flex-1 flex flex-col overflow-hidden">
      <div class="px-4 pt-4 pb-1 shrink-0">
        <h2 class="text-base font-bold text-gray-900">Chọn nhân vật mẫu</h2>
      </div>
      <div class="flex-1 p-4 overflow-y-auto pb-24 content-start grid grid-cols-2 gap-3 no-scrollbar">
        <button v-for="char in characters" :key="char.id" @click="selectedCharId = char.id"
          class="flex flex-col items-center justify-center p-4 bg-white border-2 rounded-xl transition-all aspect-4/5"
          :class="selectedCharId === char.id ? 'border-purple-500 bg-purple-50/20 ring-1 ring-purple-500' : 'border-gray-200 hover:border-gray-300'">
          <div class="w-20 h-20 flex items-center justify-center bg-gray-50 rounded-xl p-2 mb-3">
            <span class="text-4xl">{{ char.avatar }}</span>
          </div>
          <span class="text-xs font-bold text-gray-800">{{ char.name }}</span>
        </button>
      </div>
      <div class="absolute bottom-0 left-0 right-0 p-4 bg-white/95 backdrop-blur-sm border-t border-gray-100 z-20">
        <button @click="currentStep = 2" :disabled="!selectedCharId"
          class="w-full py-3.5 rounded-full font-semibold text-sm flex items-center justify-center gap-2 transition bg-purple-600 text-white hover:bg-purple-700 disabled:bg-purple-400 disabled:opacity-70">
          Tiếp theo <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5"
            stroke="currentColor" class="w-4 h-4">
            <path stroke-linecap="round" stroke-linejoin="round" d="M13.5 4.5 21 12m0 0-7.5 7.5M21 12H3" />
          </svg>
        </button>
      </div>
    </div>

    <div v-if="currentStep === 2" class="flex-1 flex flex-col overflow-hidden">

      <div class="bg-gray-50 pt-5 pb-4 flex flex-col items-center justify-center border-b border-gray-100 shrink-0">
        <div
          class="relative w-24 h-24 bg-white rounded-full border border-gray-200 shadow-sm flex items-center justify-center mb-3">
          <span class="text-5xl">{{ getSelectedCharAvatar }}</span>
          <button
            class="absolute bottom-0 right-0 bg-white p-1.5 rounded-full border border-gray-200 shadow-sm text-purple-600">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-3.5 h-3.5">
              <path
                d="M21.731 2.269a2.625 2.625 0 0 0-3.712 0l-1.157 1.157 3.712 3.712 1.157-1.157a2.625 2.625 0 0 0 0-3.712ZM19.513 8.199l-3.712-3.712-12.15 12.15a5.25 5.25 0 0 0-1.32 2.214l-.8 2.685a.75.75 0 0 0 .933.933l2.685-.8a5.25 5.25 0 0 0 2.214-1.32L19.513 8.2Z" />
            </svg>
          </button>
        </div>

        <div class="flex items-center justify-center gap-1.5 h-10 w-full px-4 overflow-x-auto no-scrollbar">
          <template v-for="(category, index) in styleCategories" :key="category.id">
            <div v-if="getSelectedIconForCategory(category.id)" @click="activeTab = category.id"
              class="flex items-center justify-center w-12 h-10 bg-white border border-gray-200 rounded-full shadow-sm text-xl cursor-pointer hover:border-purple-400 transition"
              :class="{ 'ring-2 ring-purple-500/30 border-purple-500': activeTab === category.id }">
              {{ getSelectedIconForCategory(category.id) }}
            </div>

            <span v-if="getSelectedIconForCategory(category.id) && hasNextActiveChip(index)"
              class="text-gray-300 text-sm font-bold select-none">
              +
            </span>
          </template>
        </div>
      </div>

      <div class="flex border-b border-gray-100 text-center bg-white shrink-0">
        <button v-for="tab in styleCategories" :key="tab.id" @click="activeTab = tab.id"
          class="flex-1 py-2.5 text-xs font-medium transition-all flex flex-col items-center gap-0.5 border-b-2"
          :class="activeTab === tab.id ? 'border-purple-600 text-purple-600 font-bold' : 'border-transparent text-gray-500'">
          <span class="text-lg">{{ tab.icon }}</span>
          <span>{{ tab.name }}</span>
        </button>
      </div>

      <div class="flex-1 p-4 overflow-y-auto pb-24 no-scrollbar bg-white">
        <div class="grid grid-cols-2 gap-3">
          <button v-for="item in currentTabOptions" :key="item.id" @click="selectStyle(item.id)"
            class="flex flex-col items-center justify-center p-4 bg-white border-2 rounded-xl transition relative aspect-[1.1/1]"
            :class="selectedStyles[activeTab] === item.id ? 'border-purple-500 bg-purple-50/40' : 'border-gray-200'">
            <div v-if="selectedStyles[activeTab] === item.id"
              class="absolute top-2 right-2 bg-purple-600 text-white rounded-full p-0.5 w-4 h-4 flex items-center justify-center text-[10px] font-bold">
              ✓
            </div>

            <span class="text-3xl mb-2">{{ item.icon }}</span>
            <span class="text-xs font-semibold text-gray-700 text-center">{{ item.name }}</span>
          </button>
        </div>
      </div>

      <div class="absolute bottom-0 left-0 right-0 p-4 bg-white/95 backdrop-blur-sm border-t border-gray-100 z-20">
        <button @click="submitCreateRequest" :disabled="isGenerating || !canSubmit"
          class="w-full py-3.5 rounded-full font-semibold text-sm flex items-center justify-center gap-2 transition shadow-md bg-purple-600 text-white hover:bg-purple-700 disabled:bg-gray-200 disabled:text-gray-400">
          <span v-if="isGenerating">🔮 Đang vẽ bằng AI...</span>
          <span v-else class="flex items-center gap-1.5">✨ Tạo sticker</span>
        </button>
      </div>
    </div>

    <ResultPopup :is-open="isResultPopupOpen" :img-url="generatedStickerUrl" :turns-left="remainingTurns"
      @close="isResultPopupOpen = false" @regenerate="handleRegenerate" @save="handleSaveSticker" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import ResultPopup from './ResultPopup.vue';

const emit = defineEmits(['sticker-generated']);
const currentStep = ref<number>(1);
const isMenuOpen = ref<boolean>(false);
const isGenerating = ref<boolean>(false);

// DỮ LIỆU STEP 1
const selectedCharId = ref<string>('ca');
const characters = ref([
  { id: 'ca', name: 'Cà', avatar: '🦊' },
  { id: 'ca-meo', name: 'Cà & Méo', avatar: '🐱' },
  { id: 'rong-medie', name: 'Rồng Medie', avatar: '🐲' },
  { id: 'pikalong', name: 'Pikalong', avatar: '🐊' },
  { id: 'i-am-binie', name: 'I Am Binie', avatar: '🐮' },
]);

const getSelectedCharAvatar = computed(() => {
  return characters.value.find(c => c.id === selectedCharId.value)?.avatar || '🦊';
});

// DỮ LIỆU STEP 2
const activeTab = ref<string>('emotion');
const selectedStyles = ref<Record<string, string | null>>({
  emotion: null,
  appearance: null,
  action: null,
});

// Mock data chuẩn theo đúng ảnh mẫu mới nhất bạn cung cấp
const styleCategories = ref([
  {
    id: 'emotion', name: 'Biểu cảm', icon: '😃',
    options: [
      { id: 'vui-ve', name: 'Vui vẻ', icon: '😍' },
      { id: 'dang-yeu', name: 'Đang yêu', icon: '🥰' },
    ]
  },
  {
    id: 'appearance', name: 'Ngoại hình', icon: '👕',
    options: [
      { id: 'vay', name: 'Váy ngắn', icon: '👗' },
      { id: 'ao-thun', name: 'Áo thun', icon: '👕' },
    ]
  },
  {
    id: 'action', name: 'Hành động', icon: '🏃',
    options: [
      { id: 'lam-viect', name: 'Làm việc', icon: '💻' },
      { id: 'vui-choi', name: 'Vui chơi', icon: '🎉' },
      { id: 'an-uong', name: 'Ăn uống', icon: '🍲' },
      { id: 'mua-sam', name: 'Mua sắm', icon: '🛍️' },
      { id: 'o-nha', name: 'Ở nhà', icon: '🏠' },
      { id: 'the-thao', name: 'Chơi thể thao', icon: '⚽' },
    ]
  }
]);

const currentTabOptions = computed(() => {
  return styleCategories.value.find(t => t.id === activeTab.value)?.options || [];
});

// Trả về icon của option đang được chọn trong danh mục để render lên thanh tag đầu trang
const getSelectedIconForCategory = (catId: string) => {
  const selectedItemId = selectedStyles.value[catId];
  if (!selectedItemId) return null;

  const category = styleCategories.value.find(c => c.id === catId);
  return category?.options.find(o => o.id === selectedItemId)?.icon || null;
};

// Hàm kiểm tra xem phía sau chip hiện tại còn chip nào đang kích hoạt nữa không để gắn dấu '+'
const hasNextActiveChip = (currentIndex: number): boolean => {
  return styleCategories.value.slice(currentIndex + 1).some(cat => selectedStyles.value[cat.id] !== null);
};

const selectStyle = (id: string) => {
  if (selectedStyles.value[activeTab.value] === id) {
    selectedStyles.value[activeTab.value] = null;
  } else {
    selectedStyles.value[activeTab.value] = id;
  }
};

const canSubmit = computed(() => {
  return Object.values(selectedStyles.value).some(v => v !== null);
});

// const submitCreateRequest = async () => {
//   if (!canSubmit.value || isGenerating.value) return;
//   isGenerating.value = true;
//   try {
//     await new Promise(resolve => setTimeout(resolve, 2000));
//     // Lấy icon của hành động làm đại diện sticker mới
//     const currentActionIcon = getSelectedIconForCategory('action') || '✨';
//     const mockImageResult = `https://openui.fly.dev/openui/100x100.svg?text=${currentActionIcon}`;

//     emit('sticker-generated', mockImageResult);
//     currentStep.value = 1;
//     selectedStyles.value = { emotion: null, appearance: null, action: null };
//   } catch (e) {
//     console.error(e);
//   } finally {
//     isGenerating.value = false;
//   }
// };

// Trạng thái kiểm soát đóng mở popup kết quả
const isResultPopupOpen = ref<boolean>(false);
const generatedStickerUrl = ref<string>('');
const remainingTurns = ref<number>(57); // Số lượt còn lại mô phỏng

const submitCreateRequest = async () => {
  if (!canSubmit.value || isGenerating.value) return;

  isGenerating.value = true;

  try {
    // Giả lập gọi API lên Spring Boot mất 2 giây
    await new Promise(resolve => setTimeout(resolve, 2000));

    // Giả sử đây là URL ảnh PNG trong suốt nhận được từ AI sau khi phối đồ
    generatedStickerUrl.value = 'https://openui.fly.dev/openui/200x200.svg?text=🦊✨💃';

    // Mở popup kết quả lên
    isResultPopupOpen.value = true;
  } catch (e) {
    console.error("Lỗi tạo ảnh:", e);
  } {
    isGenerating.value = false;
  }
};

// Hàm xử lý khi người dùng nhấn "Lưu sticker" từ Popup kết quả gửi ra
const handleSaveSticker = (url: string) => {
  isResultPopupOpen.value = false;
  // Bắn ảnh ra trang gốc AISticker.vue để đưa vào kho ảnh bên trái
  emit('sticker-generated', url);

  // Reset các lựa chọn cũ để sẵn sàng cho lượt tạo mới
  selectedStyles.value = { emotion: null, appearance: null, action: null };
  currentStep.value = 1;
};

// Hàm xử lý khi nhấn "Đổi kết quả khác" (Gọi lại API)
const handleRegenerate = () => {
  isResultPopupOpen.value = false;
  submitCreateRequest();
};
</script>