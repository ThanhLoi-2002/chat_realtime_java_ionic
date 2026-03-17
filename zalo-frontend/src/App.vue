<template>
  <component :is="layouts[route.meta.layout] || 'div'">
    <div class="loading-container" v-if="userStorage.isAuthLoading">
      <div class="text-center">
        <loading-spinner size="40px" />
        <p>Loading....</p>
      </div>
    </div>
    <router-view v-else />
  </component>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import AuthLayout from './layouts/AuthLayout.vue';
import MainLayout from './layouts/MainLayout.vue';
import NoLayout from './layouts/NoLayout.vue';
import LoadingSpinner from './components/Loading/LoadingSpinner.vue';
import { useLangStore } from './stores/lang';
import { onMounted, watch } from 'vue';
import { useUserStore } from './stores/user';
import { getKey } from './utils/local';
import { THEME } from './utils/constant';
import { connectSocket, disconnectSocket } from './utils/websocket';

const route: any = useRoute();
const langStore = useLangStore()
const userStorage = useUserStore()

const layouts: any = {
  main: MainLayout,
  auth: AuthLayout,
  nolayout: NoLayout
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

  langStore.getListByLang();
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
    connectSocket()
  } else {
    disconnectSocket()
  }
}, { immediate: true })
</script>
