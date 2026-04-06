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
import { ACCESS_TOKEN, THEME } from './utils/constant';
import { connectSocket, disconnectSocket } from './utils/websocket';
import { useDevice } from './composables/useDevice';
import { useSystemStore } from './stores/system.storage';

const route: any = useRoute();
const langStore = useLangStore()
const userStorage = useUserStore()
const systemStorage = useSystemStore()
const { isMobile } = useDevice()

const layouts: any = {
  main: defineAsyncComponent(() => import('./layouts/MainLayout.vue')),
  auth: defineAsyncComponent(() => import('./layouts/AuthLayout.vue')),
  nolayout: defineAsyncComponent(() => import('./layouts/NoLayout.vue'))
}

// Khởi tạo theme từ localStorage hoặc system preference
onMounted(() => {
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

watch(() => userStorage.user, () => {
  if (userStorage.user) {
    connectSocket(getKey(ACCESS_TOKEN) || '')
  } else {
    disconnectSocket()
  }
}, { immediate: true })
</script>
