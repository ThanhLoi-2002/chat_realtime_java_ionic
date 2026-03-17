<template>
    <div class="dark:bg-slate-800">
        <!-- Header: cover + avatar + close -->
        <div class="relative">
            <div class="h-32">
                <img :src="userStorage.user?.cover?.url ?? RANDOM_AVATAR" alt="cover" class="w-full h-full rounded-lg" />
            </div>

            <div class="absolute left-4 -bottom-12">
                <div class="w-20 h-20 rounded-full ring-2 ring-white dark:ring-gray-900 overflow-hidden bg-gray-300">
                    <img v-if="userStorage.user?.avatar?.url" :src="userStorage.user?.avatar?.url ?? RANDOM_AVATAR" alt="avatar" class="w-full h-full object-cover" />
                    <div v-else
                        class="w-full h-full flex items-center justify-center text-xl text-gray-600 dark:text-gray-300">
                        {{ user.name }}
                    </div>
                </div>
            </div>
        </div>

        <!-- User info -->
        <div class="flex pt-2 px-6 pb-8 border-b border-gray-100 dark:border-gray-800">
            <div class="ml-20 flex-1">
                <div class="text-lg font-semibold leading-5 truncate text-gray-600 dark:text-gray-300">{{ user.name }}</div>
                <!-- <div class="text-sm text-gray-500 dark:text-gray-400 truncate">{{ user.phone }}</div> -->
            </div>
            <ThemeToggle custom-class="p-2" />
        </div>

        <!-- Menu items -->
        <nav class="px-4 py-4 space-y-2">
            <button v-for="(item, index) in menuItems" :key="index" @click="item.onClick"
                class="w-full flex items-center gap-3 px-3 py-2 rounded-md hover:bg-gray-100 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-300 transition cursor-pointer">
                <i :class="item.icon" />
                <span class="flex-1 text-left">{{ item.title }}</span>
                <svg class="w-4 h-4 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                </svg>
            </button>

            <LangDropdown />

            <button @click="() => showConfirm = !showConfirm"
                class="w-full flex items-center gap-3 px-3 py-2 rounded-md hover:bg-gray-100 dark:hover:bg-gray-700 transition text-red-600 dark:text-red-400 cursor-pointer">
                <svg class="w-5 h-5" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M16 13v-2H7V8l-5 4 5 4v-3zM20 3h-8v2h8v14h-8v2h8a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2z" />
                </svg>
                <span class="flex-1 text-left">{{ t("logout") }}</span>
            </button>
        </nav>

        <ConfirmModal v-model:showConfirm="showConfirm" :onOk="onLogout" :message="t('logout')" :header="t('logout')"/>
    </div>
</template>
<script setup lang="ts">
import LangDropdown from '@/components/Dropdown/LangDropdown.vue';
import ConfirmModal from '@/components/Modal/ConfirmModal.vue';
import ThemeToggle from '@/components/Toggle/ThemeToggle.vue';
import { useTranslate } from '@/composables/useTranslate'
import { useUserStore } from '@/stores/user';
import { SettingPageType } from '@/types/common';
import { RANDOM_AVATAR } from '@/utils/constant'
import { computed, inject, ref } from 'vue';

const props = defineProps<{
    goPage: (value: SettingPageType) => void
}>()

const dismiss = inject<() => void>("modalDismiss")

const { t } = useTranslate();
const userStorage = useUserStore()

const showConfirm = ref(false)

const user = {
    avatar: RANDOM_AVATAR,
    cover: RANDOM_AVATAR,
    name: "Lợi",
}

const menuItems = computed(() =>[
    {
        title: t("profile"),
        icon: "fa fa-user",
        onClick: () => props.goPage('profile')
    },
])

function onLogout() {
    userStorage.logout()
    dismiss?.()
}
</script>