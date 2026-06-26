<template>
  <div class="min-h-screen bg-slate-100 flex flex-col items-center justify-center p-6 text-center">
    <h1 class="text-9xl font-bold text-blue-600">403</h1>
    <p class="text-2xl font-semibold mt-4 text-slate-800">Ối! Truy cập bị từ chối</p>
    <p class="text-slate-500 mt-2 mb-8">
      Bạn không có quyền truy cập vào trang này
    </p>
    
    <button 
      @click="goBack"
      class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-full transition-all shadow-lg"
    >
      Quay về trang chủ
    </button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';


const router = useRouter();
const goBack = () => {
  // Kiểm tra nếu có lịch sử trình duyệt thì back, không thì đẩy về trang chủ tránh bị kẹt
  if (window.history.length > 1) {
    router.back(); 
  } else {
    router.push('/');
  }
};
</script>