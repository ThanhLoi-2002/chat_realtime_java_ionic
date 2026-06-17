<template>
    <ion-header :class="[oaStyle.bg.primary, oaStyle.border.primary, oaStyle.text.primary, 'border-b']">
        <div class="flex items-center justify-between px-6 h-16">
            <button @click="sysStorage.toggleSidebar()"
                :class="[oaStyle.bg.hover, oaStyle.text.secondary, 'p-1.5 rounded cursor-pointer flex items-center justify-center']">
                <i class="fa-solid fa-bars text-sm" />
            </button>

            <nav class="flex space-x-6 text-sm font-medium mt-2">
                <router-link v-for="item in menu" :key="item.title" :to="item.to" v-slot="{ isActive }" custom>
                    <div @click="$router.push(item.to)" :class="[
                        'flex flex-col items-center cursor-pointer pb-2 border-b-2 transition-all',
                        isActive
                            ? [oaStyle.text.active, oaStyle.border.active, 'font-semibold']
                            : 'text-gray-500 border-transparent hover:text-blue-600'
                    ]">
                        <i :class="item.icon" />
                        <p class="mt-1">{{ item.title }}</p>
                    </div>
                </router-link>
            </nav>

            <a href="/chats">
                <div :class="[
                    oaStyle.text.secondary,
                    oaStyle.text.hover,
                    'flex flex-col items-center cursor-pointer',
                ]">
                    <i class="fas fa-undo"></i>
                    <p class="mt-1">{{ t('back') }}</p>
                </div>
            </a>

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
import { useTranslate } from '@/composables/useTranslate';
import { useUserStore } from '@/stores/App/user.storage';
import { useSystemStore } from '@/stores/system.storage';
import { IonHeader } from '@ionic/vue';
import { useRoute } from 'vue-router';


const route = useRoute();
const userStorage = useUserStore()
const sysStorage = useSystemStore()

interface MenuType {
    title: string
    icon: string
    to: string
}

const { t } = useTranslate()

const menu: MenuType[] = [
    {
        title: t('home'),
        icon: 'fas fa-home',
        to: 'home'
    },
    {
        title: t('structure'),
        icon: 'fas fa-sitemap',
        to: 'structure'
    },
    {
        title: t('role'),
        icon: 'fas fa-lock',
        to: 'role'
    },
]
</script>