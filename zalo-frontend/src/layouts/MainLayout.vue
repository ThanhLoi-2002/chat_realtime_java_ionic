<template>
  <ion-page>
    <ion-content :fullscreen="true" :scroll-y="false">

      <div class="flex min-h-full bg-white dark:bg-slate-800">
        <!-- :class="[!isSmartDevice() && 'my-6']" -->
        <sidebar />

        <main class="flex-1 overflow-auto">
          <slot />
        </main>

      </div>

    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import Sidebar from '@/components/App/Sidebar/Sidebar.vue';
import { useDevice } from '@/composables/useDevice';
import { useClassificationCardStore } from '@/stores/App/classificationCard.storage';
import { useFriendshipStore } from '@/stores/App/friendship.storage';
import { IonPage, IonContent } from '@ionic/vue';
import { onMounted } from 'vue';

const { isMobile, isSmartDevice } = useDevice()

const classificationCardStorage = useClassificationCardStore()
const friendshipStorage = useFriendshipStore()

onMounted(() => {
  classificationCardStorage.getList()
  friendshipStorage.getFriends()
})
</script>
