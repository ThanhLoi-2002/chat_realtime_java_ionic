<template>
  <ion-app>
    <component :is="layouts[route.meta.layout] || 'div'">
      <div class="loading-container" v-if="userStorage.isAuthLoading">
        <div class="text-center">
          <loading-spinner size="40px" />
          <p>Loading....</p>
        </div>
      </div>
      <router-view v-else />
    </component>
  </ion-app>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import LoadingSpinner from './components/Loading/LoadingSpinner.vue';
import { useLangStore } from './stores/lang.storage';
import { defineAsyncComponent, onMounted, watch } from 'vue';
import { useUserStore } from './stores/user.storage';
import { getKey } from './utils/local';
import { THEME } from './utils/constant';
import { connectSocket, disconnectSocket } from './utils/websocket';
import { useDevice } from './composables/useDevice';
import { useSystemStore } from './stores/system.storage';
import { Network } from '@capacitor/network';
import { toast } from './utils/toast';
import { usePushNotification } from './composables/usePushNotification';

const route: any = useRoute();
const langStore = useLangStore()
const userStorage = useUserStore()
const systemStorage = useSystemStore()
const { initPush } = usePushNotification()
const { isMobile, logDeviceInfo, isSmartDevice } = useDevice()

const layouts: any = {
  main: defineAsyncComponent(() => import('./layouts/MainLayout.vue')),
  auth: defineAsyncComponent(() => import('./layouts/AuthLayout.vue')),
  nolayout: defineAsyncComponent(() => import('./layouts/NoLayout.vue'))
}

logDeviceInfo()

// Khởi tạo theme từ localStorage hoặc system preference
onMounted(async () => {
  if (getKey(THEME) === 'dark' ||
    (!(THEME in localStorage) &&
      window.matchMedia('(prefers-color-scheme: dark)').matches)) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }

  systemStorage.setIsDarkMode(getKey(THEME) === 'dark')

  langStore.getListByLang();

  isMobile && systemStorage.setShowBottomMenu(true)

  //save fcm to server
  initPush()

  // 2. Lắng nghe sự kiện thay đổi mạng
  Network.addListener('networkStatusChange', status => {
    systemStorage.checkNetworkStatus()
    toast({
      message: status.connected ? 'Đã kết nối mạng' : 'Bạn đang offline. Một số tính năng có thể bị hạn chế.',
      color: status.connected ? 'success' : 'danger',
    })

    if (status.connected) {
      // Đợi 1 chút để Toast kịp hiển thị hoặc để mạng ổn định hẳn
      setTimeout(() => {
        window.location.reload();
      }, 2500);
    }
  });
})

// Optional: theo dõi system thay đổi
watch(() => window.matchMedia('(prefers-color-scheme: dark)').matches, (prefersDark) => {
  if (!(THEME in localStorage)) {
    if (prefersDark) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }
})

watch(() => userStorage.user, async (newVal, oldVal) => {
  if (newVal) {
    // Nếu trước đó đã có user (đổi user mà không qua bước null)
    // thì phải ngắt cái cũ trước
    if (oldVal) {
      await disconnectSocket();
    }
    await connectSocket();
  } else {
    await disconnectSocket();
  }
}, { immediate: true });
</script>
