<template>
  <div class="fixed inset-0 z-50 font-sans select-none transition-all duration-300"
       :class="[isOpen ? 'opacity-100 pointer-events-auto' : 'opacity-0 pointer-events-none']">
    
    <div @click="$emit('close')" class="absolute inset-0 bg-black/60 backdrop-blur-sm"></div>

    <div 
      class="absolute bottom-0 left-0 right-0 max-w-md mx-auto bg-white rounded-t-3xl p-5 pb-8 shadow-2xl transition-transform duration-350 transform flex flex-col items-center"
      :class="[isOpen ? 'translate-y-0' : 'translate-y-full']"
    >
      <div class="w-10 h-1 bg-gray-200 rounded-full mb-4"></div>

      <button 
        @click="$emit('close')" 
        class="absolute top-4 right-4 text-gray-400 hover:text-gray-600 p-1 rounded-full hover:bg-gray-50"
      >
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-5 h-5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
        </svg>
      </button>

      <div class="w-48 h-48 bg-pink-100 rounded-2xl p-4 mb-5 relative flex items-center justify-center overflow-hidden shadow-inner border border-pink-50">
        <div class="absolute inset-0 opacity-20 pointer-events-none bg-[radial-gradient(#ec4899_1px,transparent_1px)] bg-size-[16px_16px]"></div>
        
        <img :src="imgUrl" alt="Kết quả Sticker AI" class="w-full h-full object-contain relative z-10 select-none animate-scaleUp" />
      </div>

      <h3 class="text-lg font-bold text-gray-900 mb-1">Tạo sticker thành công</h3>
      <p class="text-xs text-gray-500 mb-6">
        Còn <span class="font-bold text-gray-700">turns-left /60</span> lượt tạo sticker AI hôm nay
      </p>

      <div class="w-full flex flex-col gap-3 px-2">
        <button 
          @click="$emit('regenerate')"
          class="w-full py-3.5 bg-gray-100 hover:bg-gray-200 text-gray-800 font-semibold text-sm rounded-full transition flex items-center justify-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-4 h-4 animate-spin-slow">
            <path stroke-linecap="round" stroke-linejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99" />
          </svg>
          Đổi kết quả khác
        </button>

        <button 
          @click="$emit('save', imgUrl)"
          class="w-full py-3.5 bg-purple-100 hover:bg-purple-200 text-purple-700 font-bold text-sm rounded-full transition flex items-center justify-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-4 h-4">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v6m3-3H9m12 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
          </svg>
          Lưu sticker
        </button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  isOpen: boolean;
  imgUrl: string;
  turnsLeft: number;
}>();

defineEmits(['close', 'regenerate', 'save']);
</script>

<style scoped>
/* Hiệu ứng zoom nhẹ cho bức ảnh sticker khi hiện hình */
@keyframes scaleUp {
  from { transform: scale(0.85); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
.animate-scaleUp {
  animation: scaleUp 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

/* Xoay nhẹ icon làm mới kết quả */
.animate-spin-slow {
  transition: transform 0.5s ease;
}
button:hover .animate-spin-slow {
  transform: rotate(180deg);
}
</style>