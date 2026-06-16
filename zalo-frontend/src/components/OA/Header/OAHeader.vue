<template>
    <ion-header :class="[oaStyle.bg.primary, oaStyle.border.primary, oaStyle.text.primary, 'border-b']">
        <div class="flex items-center justify-between px-6 h-16">
            <div class="flex items-center space-x-2">
                <span class="text-2xl font-bold text-blue-600 dark:text-blue-400">Zalo</span>
                <span class="text-xs text-gray-500 hidden md:inline border-l pl-2 dark:border-gray-700">Official Account
                    Manager</span>
            </div>

            <nav class="flex space-x-6 text-sm font-medium mt-2">
                <router-link v-for="item in menu" :key="item.title" :to="item.to" v-slot="{ isActive }" custom>
                    <div @click="$router.push(item.to)" :class="[
                        'flex flex-col items-center cursor-pointer pb-2 border-b-2 transition-all',
                        isActive
                            ?  [oaStyle.text.active, oaStyle.border.active, 'font-semibold']
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
                <div
                    :class="[oaStyle.border.primary, 'flex items-center space-x-2 px-2 cursor-pointer border-l-2 border-r-2']">
                    <circle-avatar size="size-7" />
                    <span class="text-sm font-medium hidden sm:inline">SVoucher</span>
                    <i class="fas fa-chevron-down"/>
                </div>

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
import { IonHeader } from '@ionic/vue';
import { useRoute } from 'vue-router';


const route = useRoute();
const userStorage = useUserStore()

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
        title: t('chat'),
        icon: 'fas fa-comments',
        to: 'chat'
    },
    {
        title: t('chatbot'),
        icon: 'fas fa-robot',
        to: 'chatbot'
    },
    {
        title: t('content'),
        icon: 'fas fa-scroll',
        to: 'content'
    },
    {
        title: t('statistical'),
        icon: 'fas fa-signal',
        to: 'statistical'
    },
    {
        title: t('management'),
        icon: 'fas fa-cog',
        to: 'management'
    },
    {
        title: t('advertisement'),
        icon: 'fas fa-ad',
        to: 'advertisement'
    },
    {
        title: t('other'),
        icon: 'fas fa-th-large',
        to: 'other'
    },

]
</script>