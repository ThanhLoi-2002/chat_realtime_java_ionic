<template>
  <div class="max-w-md mx-auto bg-white min-h-screen flex flex-col font-sans text-slate-800 relative shadow-md">
    
    <header class="flex items-center justify-between px-4 py-3 border-b border-gray-100">
      <div class="flex items-center gap-3">
        <button class="p-1 hover:bg-gray-100 rounded-full">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
          </svg>
        </button>
        <h1 class="text-xl font-semibold text-gray-800">zSticker AI</h1>
      </div>
      <div class="flex items-center gap-2 bg-gray-50 border border-gray-200 rounded-full px-3 py-1">
        <span class="text-lg font-bold tracking-widest leading-none">•••</span>
        <div class="w-px h-4 bg-gray-300 mx-1"></div>
        <button class="text-gray-600 hover:text-black">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </header>

    <div class="bg-gray-50 py-8 flex flex-col items-center justify-center relative border-b border-gray-100">
      <div class="relative w-28 h-28 bg-white rounded-full border border-gray-200 shadow-sm flex items-center justify-center overflow-visible">
        <span class="text-6xl">🦊</span> 
        
        <button class="absolute bottom-0 right-0 bg-white p-1.5 rounded-full border border-gray-200 shadow-md text-purple-600 hover:bg-purple-50 transition">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-4 h-4">
            <path d="M21.731 2.269a2.625 2.625 0 0 0-3.712 0l-1.157 1.157 3.712 3.712 1.157-1.157a2.625 2.625 0 0 0 0-3.712ZM19.513 8.199l-3.712-3.712-12.15 12.15a5.25 5.25 0 0 0-1.32 2.214l-.8 2.685a.75.75 0 0 0 .933.933l2.685-.8a5.25 5.25 0 0 0 2.214-1.32L19.513 8.2Z" />
          </svg>
        </button>
      </div>
      <p class="text-gray-400 text-sm mt-4 px-8 text-center leading-relaxed">
        Chọn thêm các chi tiết bên dưới để "chế tạo" sticker
      </p>
    </div>

    <div class="flex border-b border-gray-100 text-center sticky top-0 bg-white z-10">
      <button 
        v-for="tab in categories" 
        :key="tab.id"
        @click="activeTab = tab.id"
        class="flex-1 py-3 text-sm font-medium transition-all flex flex-col items-center gap-1 border-b-2"
        :class="activeTab === tab.id ? 'border-purple-600 text-purple-600 font-semibold' : 'border-transparent text-gray-500 hover:text-gray-700'"
      >
        <span class="text-xl">{{ tab.icon }}</span>
        <span>{{ tab.name }}</span>
      </button>
    </div>

    <div class="flex-1 p-4 overflow-y-auto pb-24">
      <div class="grid grid-cols-3 gap-3">
        <button 
          v-for="item in currentOptions" 
          :key="item.id"
          @click="toggleSelect(item.id)"
          class="flex flex-col items-center justify-center p-4 bg-white border rounded-xl transition duration-200"
          :class="selectedOptions[activeTab] === item.id 
            ? 'border-purple-500 bg-purple-50/50 shadow-sm' 
            : 'border-gray-200 hover:border-gray-300'"
        >
          <span class="text-3xl mb-2 select-none">{{ item.icon }}</span>
          <span class="text-xs font-medium text-gray-700 text-center">{{ item.name }}</span>
        </button>
      </div>
    </div>

    <div class="absolute bottom-0 left-0 right-0 p-4 bg-white/95 backdrop-blur-sm border-t border-gray-100">
      <button 
        @click="generateSticker"
        :disabled="!canGenerate"
        class="w-full py-3.5 rounded-full font-semibold text-base flex items-center justify-center gap-2 transition shadow-sm"
        :class="canGenerate 
          ? 'bg-purple-600 text-white hover:bg-purple-700 active:scale-[0.99]' 
          : 'bg-gray-200 text-gray-400 cursor-not-allowed'"
      >
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M9.813 15.904 9 21l8.982-8.983m-10.43 3.42L3 12m5.745-4.054L19.25 3.75l-5.74 14.823m-4.697-9.724 1.116 1.116m8-3.223-1.116 1.116m-13.14 1.116a15 15 0 0 1 10.425 4.694" />
        </svg>
        Tạo sticker
      </button>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

export interface OptionItem {
  id: string;
  name: string;
  icon: string; // Có thể là emoji hoặc đường dẫn ảnh
}

export interface TabCategory {
  id: 'emotion' | 'appearance' | 'action';
  name: string;
  icon: string;
  options: OptionItem[];
}

// Mock dữ liệu giống hệt ảnh mẫu
const categories = ref<TabCategory[]>([
  {
    id: 'emotion',
    name: 'Biểu cảm',
    icon: '😃',
    options: [
      { id: 'vui-ve', name: 'Vui vẻ', icon: '😁' },
      { id: 'dang-yeu', name: 'Đang yêu', icon: '😍' },
      { id: 'ngac-nhien', name: 'Ngạc nhiên', icon: '😲' },
      { id: 'met-moi', name: 'Mệt mỏi', icon: '😴' },
      { id: 'buon-ba', name: 'Buồn bã', icon: '😭' },
      { id: 'tuc-gian', name: 'Tức giận', icon: '😡' },
    ]
  },
  {
    id: 'appearance',
    name: 'Ngoại hình',
    icon: '👕',
    options: [
      { id: 'ao-thun', name: 'Áo thun', icon: '👕' },
      { id: 'ao-len', name: 'Áo len', icon: ' sweater' },
      { id: 'kinh-mat', name: 'Kính mát', icon: '😎' },
    ]
  },
  {
    id: 'action',
    name: 'Hành động',
    icon: '🏃',
    options: [
      { id: 'chay', name: 'Đang chạy', icon: '🏃' },
      { id: 'nhay', name: 'Nhảy múa', icon: '💃' },
      { id: 'vay-tay', name: 'Vẫy tay', icon: '👋' },
    ]
  }
]);

// Tab đang được chọn
const activeTab = ref<'emotion' | 'appearance' | 'action'>('emotion');

// Lưu trạng thái các option được chọn theo từng danh mục
const selectedOptions = ref<Record<string, string | null>>({
  emotion: null,
  appearance: null,
  action: null,
});

// Lấy danh sách options của tab hiện tại
const currentOptions = computed(() => {
  return categories.value.find(c => c.id === activeTab.value)?.options || [];
});

// Chọn hoặc bỏ chọn option
const toggleSelect = (itemId: string) => {
  if (selectedOptions.value[activeTab.value] === itemId) {
    selectedOptions.value[activeTab.value] = null; // Bỏ chọn nếu click lại
  } else {
    selectedOptions.value[activeTab.value] = itemId;
  }
};

// Điều kiện kích hoạt nút Tạo: Phải chọn ít nhất 1 thuộc tính (hoặc chỉnh lại theo ý bạn)
const canGenerate = computed(() => {
  return Object.values(selectedOptions.value).some(value => value !== null);
});

// Hàm xử lý gửi data lên Spring Boot
const generateSticker = () => {
  if (!canGenerate.value) return;
  
  // Pack data lại gửi lên Backend để xử lý build prompt AI
  const payload = {
    emotion: selectedOptions.value.emotion,
    appearance: selectedOptions.value.appearance,
    action: selectedOptions.value.action,
  };
  
  console.log('Gửi yêu cầu tạo sticker với cấu hình:', payload);
  // Thực hiện call API axios tại đây...
};
</script>

<style scoped>
/* Tùy chỉnh thanh cuộn nếu danh sách quá dài */
.overflow-y-auto {
  scrollbar-width: none;
}
.overflow-y-auto::-webkit-scrollbar {
  display: none;
}
</style>