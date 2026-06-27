<template>
  <div :class="[oaStyle.bg.primary, oaStyle.text.primary, 'w-full h-screen flex flex-col md:flex-row overflow-hidden']">

    <header
      :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'md:hidden flex items-center justify-between px-4 py-3 border-b']">
      <div :class="[oaStyle.text.primary, 'text-lg font-medium tracking-wide']">✨ zSticker AI Studio</div>
      <button @click="mobileActiveTab = mobileActiveTab === 'history' ? 'create' : 'history'"
        class="bg-purple-600 hover:bg-purple-700 text-xs font-semibold px-3 py-1.5 rounded-full transition">
        {{ mobileActiveTab === 'history' ? 'Mở Trình Tạo 🪄' : 'Xem Kho Ảnh 📁' }}
      </button>
    </header>

    <section :class="[oaStyle.border.primary, oaStyle.bg.secondary, 'w-full md:w-80 lg:w-96 border-r flex-col h-full transition-all duration-300',
    mobileActiveTab === 'history' ? 'flex' : 'hidden md:flex'
    ]">
      <div :class="[oaStyle.border.primary, 'p-4 border-b flex items-center justify-between']">
        <div class="flex items-center gap-2">
          <span class="text-xl">📁</span>
          <div :class="[oaStyle.text.primary, 'font-medium text-lg']">Sticker của bạn</div>
        </div>
        <span :class="[oaStyle.text.primary, oaStyle.bg.tertiary, 'text-xs px-2 py-0.5 rounded-md']">
          {{ createdStickers.length }} bộ
        </span>
      </div>

      <div class="flex-1 p-4 overflow-y-auto grid grid-cols-3 gap-3 content-start custom-scrollbar">
        <div v-for="sticker in createdStickers" :key="sticker.id"
          class="group relative aspect-square bg-slate-800 border border-slate-700/50 hover:border-purple-500 rounded-xl p-2 flex items-center justify-center cursor-pointer transition hover:scale-105"
          @click="sendStickerToChat(sticker.url)">
          <img :src="sticker.url" class="w-full h-full object-contain select-none" alt="AI Sticker" />

          <div
            class="absolute inset-0 bg-black/40 rounded-xl opacity-0 group-hover:opacity-100 items-center justify-center transition hidden md:flex">
            <span class="text-xs font-semibold bg-purple-600 px-2 py-1 rounded-md">Gửi</span>
          </div>
        </div>

        <div v-if="createdStickers.length === 0"
          :class="[oaStyle.text.primary, 'col-span-3 text-center py-20 text-sm']">
          <span class="text-3xl block mb-2">🎨</span>
          Chưa có sticker nào.<br>Hãy tạo sticker đầu tiên!
        </div>
      </div>
    </section>


    <section :class="[oaStyle.bg.secondary, 'flex-1 flex-col h-full relative',
    mobileActiveTab === 'create' ? 'flex' : 'hidden md:flex'
    ]">
      <div class="flex-1 overflow-y-auto flex items-center justify-center p-0 md:p-6">

        <div
          class="w-full max-w-md h-[85vh] md:border md:border-slate-700/80 md:rounded-3xl md:shadow-2xl overflow-hidden bg-white text-slate-800 flex flex-col relative">

          <SelectAISticker @sticker-generated="onStickerGenerated" class="w-full h-full" />

        </div>

      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import SelectAISticker from './components/SelectAISticker.vue';
import { oaStyle } from '@/assets/tailwindcss.ts';

// Định nghĩa kiểu dữ liệu sticker lịch sử
interface GeneratedSticker {
  id: string;
  url: string;
}

// Tab tích hợp riêng cho màn hình Mobile ('history': Xem kho ảnh | 'create': Trình tạo sticker)
const mobileActiveTab = ref<'history' | 'create'>('create');

// Mock data danh sách sticker user đã tạo trước đó (Thay bằng dữ liệu lấy từ DB Spring Boot của bạn)
const createdStickers = ref<GeneratedSticker[]>([
  { id: '1', url: 'https://openui.fly.dev/openui/100x100.svg?text=🦊' },
  { id: '2', url: 'https://openui.fly.dev/openui/100x100.svg?text=🐱' },
  { id: '3', url: 'https://openui.fly.dev/openui/100x100.svg?text=🐲' },
  { id: '4', url: 'https://openui.fly.dev/openui/100x100.svg?text=🐊' },
]);

// Callback kích hoạt khi từ component con (SelectAISticker) báo về là đã gọi API Spring Boot tạo ảnh xong
const onStickerGenerated = (newStickerUrl: string) => {
  const newSticker: GeneratedSticker = {
    id: Date.now().toString(),
    url: newStickerUrl
  };
  // Đẩy lên đầu danh sách hiển thị phía bên trái
  createdStickers.value.unshift(newSticker);

  // Trên mobile, tự động nhảy về tab kho ảnh để user thấy sticker mới tạo
  mobileActiveTab.value = 'history';
};

// Hàm xử lý click chọn sticker để đẩy thẳng vào ô chat chính qua WebSocket
const sendStickerToChat = (url: string) => {
  console.log("Đã chọn gửi sticker vào hội thoại:", url);
  // Thực hiện emit ra phòng chat lớn hoặc call hàm gửi WebSocket chat tại đây
};
</script>