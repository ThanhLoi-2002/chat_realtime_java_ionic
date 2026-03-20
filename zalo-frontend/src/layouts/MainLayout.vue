<template>
  <ion-page>
    <ion-content :fullscreen="true" :scroll-y="false">

      <div class="flex h-full bg-white dark:bg-slate-800">
        <sidebar />

        <main
          class="flex-1 overflow-auto"
        >
          <slot />
        </main>

      </div>

    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import Sidebar from '@/components/Sidebar/Sidebar.vue';
import { useDevice } from '@/composables/useDevice';
import { IonPage, IonContent } from '@ionic/vue';

const { isMobile } = useDevice()
</script>
