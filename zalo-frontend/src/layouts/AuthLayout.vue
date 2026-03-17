<script setup lang="ts">
import ThemeToggle from "@/components/Toggle/ThemeToggle.vue";
import { IonContent, IonPage } from '@ionic/vue'
</script>

<template>
  <ion-page>

    <ion-content>
      <!-- background toàn trang -->
      <div class="bg-gray-100 dark:bg-gray-900 min-h-screen transition-colors duration-300">

        <!-- toggle -->
        <div class="absolute top-4 right-4">
          <theme-toggle />
        </div>

        <div class="flex min-h-screen">

          <!-- LEFT BRANDING -->
          <div class="hidden lg:flex lg:w-1/2 flex-col justify-center items-center 
                   bg-primary/10 dark:bg-primary/20 p-12 transition-colors">

            <div class="flex items-center gap-2 mb-4">
              <img class="w-full h-auto rounded-3xl"
                data-alt="Abstract modern communication illustration with chat bubbles"
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuCfXa9gTIFtfZUEOoRZc7Xi_n_6BBBy-zoglWcGdxYQWbguPPXFfM8sptEYyq1unMb4tJHwHiuaGOywpHvwHNZmF2MJdOlhElVeRdjFH0S1z8S_QdMBaw4pDG0HTrLEUMITJn3xceYaiXkJPTWsaEmv_7PLSZRGuh-uGLpa9HvHG-eozKIh6qe4F_8_nsiv2tQyca5y9VLsJ9JbF2GAivHtzc3dawoMCftXw4JSuB_2iA6lVdD3LADxtqfGM_uPM4NhLKBs73kXEwM4" />
            </div>

            <div class="max-w-md text-center">

              <h1 class="text-4xl font-bold mb-4 text-gray-900 dark:text-white">
                Connect with friends effortlessly
              </h1>

              <p class="text-gray-500 dark:text-gray-400">
                Fast and secure messaging platform
              </p>
              <div class="absolute bottom-10 flex gap-6 text-sm font-medium text-slate-500">
                <a class="hover:text-primary" href="#">Privacy Policy</a>
                <a class="hover:text-primary" href="#">Terms of Service</a>
                <a class="hover:text-primary" href="#">Help Center</a>
              </div>
            </div>
          </div>

          <!-- RIGHT LOGIN -->
          <div class="w-full lg:w-1/2 flex items-center justify-center p-6
                   bg-white dark:bg-gray-800 transition-colors">
            <slot />

          </div>

        </div>

      </div>
    </ion-content>

  </ion-page>
</template>