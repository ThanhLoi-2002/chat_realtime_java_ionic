<template>
    <ion-page>
        <OAHeader />
        <ion-content :scroll-y="false">
            <div :class="['flex h-full w-full overflow-hidden', oaStyle.bg.primary]">
                <OASidebar />
                <main class="flex-1 h-full overflow-auto">
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
import { getKey } from '@/utils/local';
import { OA_ID, OA_ROUTE } from '@/utils/constant';
import router from '@/router';
import { useOaStore } from '@/stores/Oa/oa.storage';

const structureStor = useAdminStructureStore()
const oaStor = useOaStore()
onMounted(async () => {
    const oaId = getKey(OA_ID)

    if (!oaId) {
        router.push(OA_ROUTE.accounts)
        return
    }

    await Promise.allSettled([
        await oaStor.getById(+oaId),
        await oaStor.getAllOasActive(),

        await structureStor.getMenuByUser(AppTypeEnum.OA)
    ])

})
</script>
