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

    <confirm-modal v-model:showConfirm="confirmStore.show" :header="confirmStore.title" :message="confirmStore.message"
      :onOk="confirmStore.confirm" />

    <avatar-modal />
  </ion-app>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import LoadingSpinner from './components/Shared/Loading/LoadingSpinner.vue';
import { useLangStore } from './stores/App/lang.storage.ts';
import { onMounted, watch } from 'vue';
import { useUserStore } from './stores/App/user.storage.ts';
import AvatarModal from './components/Shared/Avatar/AvatarModal.vue';
import { getKey } from './utils/local';
import { THEME } from './utils/constant';
import { connectSocket, disconnectSocket } from './utils/websocket';
import { useDevice } from './composables/useDevice';
import { useSystemStore } from './stores/system.storage.ts';
import { Network } from '@capacitor/network';
import { toast } from './utils/toast';
import { usePushNotification } from './composables/usePushNotification';
import { storage } from './services/storage.service.';
import { useConfirmStore } from './composables/useConfirm';
import MainLayout from './layouts/MainLayout.vue';
import AuthLayout from './layouts/AuthLayout.vue';
import NoLayout from './layouts/NoLayout.vue';
import OALayout from './layouts/OALayout.vue'
import AdminLayout from './layouts/AdminLayout.vue';

const route: any = useRoute();
const langStore = useLangStore()
const userStorage = useUserStore()
const systemStorage = useSystemStore()
const { initPush } = usePushNotification()
const { isMobile, logDeviceInfo, isSmartDevice } = useDevice()
const confirmStore = useConfirmStore();

const layouts: any = {
  main: MainLayout,
  auth: AuthLayout,
  oa: OALayout,
  admin: AdminLayout,
  nolayout: NoLayout
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

  storage.checkTotalQuota()
  // Sử dụng
  let size = await storage.getStorageSize('conversations');
  console.log(`Dung lượng danh sách chat: ${size}`);

  size = await storage.getStorageSize('user');
  console.log(`Dung lượng user: ${size}`);

  await storage.calculateAllKeysSize()
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

    initPush()
    await connectSocket();
  } else {
    await disconnectSocket();
    // userStorage.logout()
  }
}, { immediate: true });
</script>
