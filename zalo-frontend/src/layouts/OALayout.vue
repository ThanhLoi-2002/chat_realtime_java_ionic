<template>
    <ion-page>
        <OAHeader />
        <ion-content :scroll-y="false">
            <div :class="['flex h-full w-full overflow-hidden', oaStyle.bg.primary]">
                <OASidebar />
                <main class="flex-1 h-full overflow-auto py-2 px-4">
                    <slot />
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

</script>
