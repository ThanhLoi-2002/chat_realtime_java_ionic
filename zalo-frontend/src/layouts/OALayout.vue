<template>
    <ion-page>
        <OAHeader />
        <ion-content :scroll-y="false">
            <div :class="['flex h-full w-full overflow-hidden', oaStyle.bg.primary]">
                <OASidebar />
                <main class="flex-1 h-full overflow-auto py-2 px-4">
                    <router-view />
                </main>
            </div>
        </ion-content>
    </ion-page>
</template>

<script setup lang="ts">
import OASidebar from '@/components/OA/Sidebar/OASidebar.vue';
import OAHeader from '@/components/OA/Header/OAHeader.vue';
import { IonPage, IonContent } from '@ionic/vue';
import { oaStyle } from '@/assets/tailwindcss';
import { onMounted } from 'vue';
import { useAdminStructureStore } from '@/stores/Admin/structure.storage';
import { AppTypeEnum } from '@/types/enum';

const structureStor = useAdminStructureStore()
onMounted(async () => {
    await structureStor.getMenuByUser(AppTypeEnum.OA)
})
</script>
