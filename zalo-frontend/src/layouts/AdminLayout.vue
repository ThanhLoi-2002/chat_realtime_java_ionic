<template>
    <ion-page>
        <admin-header />
        <ion-content :scroll-y="false">
            <div :class="['flex h-full w-full overflow-hidden', oaStyle.bg.primary]">
                <admin-sidebar />
                <main class="flex-1 h-full overflow-auto py-2 px-4">
                    <router-view />
                </main>
            </div>
        </ion-content>
    </ion-page>
</template>

<script setup lang="ts">
import { IonPage, IonContent } from '@ionic/vue';
import { oaStyle } from '@/assets/tailwindcss';
import AdminSidebar from '@/components/Admin/Sidebar/AdminSidebar.vue';
import AdminHeader from '@/components/Admin/Header/AdminHeader.vue';
import { onMounted } from 'vue';
import { useAdminStructureStore } from '@/stores/Admin/structure.storage';
import { AppTypeEnum } from '@/types/enum';
import { useMenuStore } from '@/stores/menu.storage';

const structureStor = useAdminStructureStore()
const menuStor = useMenuStore()

onMounted(async () => {
    menuStor.resetLoadState()

    await Promise.allSettled([
        await structureStor.getMenuByUser(AppTypeEnum.ADMIN),
        await menuStor.switchApp(AppTypeEnum.ADMIN)
    ])
})
</script>
