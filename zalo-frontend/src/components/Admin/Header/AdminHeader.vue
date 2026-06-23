<template>
    <ion-header :class="[oaStyle.bg.primary, oaStyle.border.primary, oaStyle.text.primary, 'border-b']">
        <div class="flex items-center justify-between px-6 h-16">
            <div :class="[oaStyle.border.primary, 'flex items-center justify-between w-58 pr-4 border-r']">
                <button @click="sysStorage.toggleSidebar()"
                    :class="[oaStyle.bg.hover, oaStyle.text.secondary, 'p-1.5 rounded cursor-pointer flex items-center justify-center']">
                    <i class="fa-solid fa-bars text-sm" />
                </button>

                <a href="/oa-dashboard/home">
                    <div :class="[
                        oaStyle.text.secondary,
                        oaStyle.text.hover,
                        'flex flex-col items-center cursor-pointer',
                    ]">
                        <i class="fas fa-undo text-sm"></i>
                        <p class="mt-1">{{ t('oa') }}</p>
                    </div>
                </a>

                <a href="/chats">
                    <div :class="[
                        oaStyle.text.secondary,
                        oaStyle.text.hover,
                        'flex flex-col items-center cursor-pointer',
                    ]">
                        <i class="fas fa-undo text-sm"></i>
                        <p class="mt-1">{{ t('chat') }}</p>
                    </div>
                </a>
            </div>

            <div class="flex-1 px-4">
                <nav class="flex space-x-6 text-sm font-medium mt-2">
                    <router-link v-for="item in getMenusByType(2)" :key="item.code" :to="item.path" custom>
                        <div v-if="item.children.length > 0" @click="$router.push(buildFirstRoute(item))" :class="[
                            'flex flex-col items-center cursor-pointer pb-2 border-b-2 transition-all',
                            checkRouteActive(item.path)
                                ? [oaStyle.text.active, oaStyle.border.active, 'font-semibold']
                                : 'text-gray-500 border-transparent hover:text-blue-600'
                        ]">
                            <p v-html="item.icon" />
                            <p class="mt-1">{{ item.code }}</p>
                        </div>
                    </router-link>
                </nav>
            </div>

            <div class="flex items-center space-x-4">
                <theme-toggle custom-class="p-2" />
                <circle-avatar size="size-7" :user="userStorage.user" />
            </div>
        </div>
    </ion-header>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import CircleAvatar from '@/components/Shared/Avatar/CircleAvatar.vue';
import { useStructure } from '@/composables/useStructure';
import { useTranslate } from '@/composables/useTranslate';
import { useUserStore } from '@/stores/App/user.storage';
import { useSystemStore } from '@/stores/system.storage';
import { StructureType } from '@/types/entities';
import { IonHeader } from '@ionic/vue';
import { useRoute } from 'vue-router';


const route = useRoute();
const userStorage = useUserStore()
const sysStorage = useSystemStore()
const { getMenusByType, checkRouteActive } = useStructure()

const { t } = useTranslate()

const buildFirstRoute = (node: StructureType) => {
    console.log(node.children.length > 0 ? node.children[0].path : '')
    return node.children.length > 0 ? node.children[0].path : ''
}

</script>