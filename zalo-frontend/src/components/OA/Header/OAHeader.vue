<template>
    <ion-header :class="[oaStyle.bg.primary, oaStyle.border.primary, oaStyle.text.primary, 'border-b']">
        <div class="flex items-center justify-between px-6 h-16 gap-4">
            <div class="flex items-center w-58">
                <button @click="sysStorage.toggleSidebar()"
                    :class="[oaStyle.bg.hover, oaStyle.text.secondary, 'p-1.5 rounded cursor-pointer flex items-center justify-center']">
                    <i class="fa-solid fa-bars text-sm" />
                </button>
                <span class="text-md hidden md:inline text-gray-500 pl-1 font-bold">OA Manager</span>
            </div>

            <nav class="flex space-x-6 text-sm font-medium mt-2">
                <router-link v-for="item in getMenusByType(2)" :key="item.id" :to="item.path" custom>
                    <div @click="$router.push(item.path)" :class="[
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

            <a :href="ADMIN_ROUTE.home">
                <div :class="[
                    oaStyle.text.secondary,
                    oaStyle.text.hover,
                    'flex flex-col items-center cursor-pointer',
                ]">
                    <i class="fas fa-undo text-sm"></i>
                    <p class="mt-1">{{ t('admin') }}</p>
                </div>
            </a>

            <a :href="APP_ROUTE.index">
                <div :class="[
                    oaStyle.text.secondary,
                    oaStyle.text.hover,
                    'flex flex-col items-center cursor-pointer',
                ]">
                    <i class="fas fa-undo text-sm"></i>
                    <p class="mt-1">{{ t('chat') }}</p>
                </div>
            </a>

            <div class="flex items-center space-x-4">
                <theme-toggle custom-class="p-2" />
                <div
                    :class="[oaStyle.border.primary, 'flex items-center space-x-2 px-2 cursor-pointer border-l-2 border-r-2']">
                    <circle-avatar size="size-7" />
                    <span class="text-sm font-medium hidden sm:inline">SVoucher</span>
                    <i class="fas fa-chevron-down" />
                </div>

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
import { ADMIN_ROUTE, APP_ROUTE } from '@/utils/constant';
import { IonHeader } from '@ionic/vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const userStorage = useUserStore()
const sysStorage = useSystemStore()
const { getMenusByType, checkRouteActive } = useStructure()

interface MenuType {
    title: string
    icon: string
    to: string
}

const { t } = useTranslate()
</script>