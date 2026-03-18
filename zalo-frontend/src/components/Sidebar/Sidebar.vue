<template>
    <!-- DESKTOP SIDEBAR -->
    <aside v-if="!isMobile" class="w-16 flex flex-col items-center py-2 border-r
        border-slate-200 dark:border-slate-500
        bg-white dark:bg-gray-800 gap-2
        sticky top-0 h-screen">

        <CircleAvatar :url="userStorage.user?.avatar?.url ?? RANDOM_AVATAR" size="size-10" id="openModal" :onClick="() => goPage('setting')" />

        <nav class="flex flex-col gap-2 flex-1">

            <router-link v-for="item in visibleItems" :key="item.to" :to="item.to">
                <i :class="[
                    item.icon,
                    'p-4 rounded-lg dark:text-slate-300',
                    route.path.startsWith(item.to)
                        ? 'bg-gray-200 dark:bg-gray-600'
                        : 'hover:bg-gray-200 dark:hover:bg-gray-600'
                ]" />
            </router-link>

        </nav>

        <!-- <ThemeToggle /> -->

    </aside>


    <!-- MOBILE BOTTOM MENU -->
    <nav v-else class="fixed bottom-0 left-0 right-0
        h-12 flex items-center justify-around
        border-t border-slate-200 dark:border-slate-500
        bg-white dark:bg-gray-800 z-10
        pb-[env(safe-area-inset-bottom)]">

        <router-link v-for="item in visibleItems" :key="item.to" :to="item.to"
            class="flex flex-col items-center justify-center">

            <i :class="[
                item.icon,
                'text-xl',
                route.path.startsWith(item.to)
                    ? 'text-blue-500'
                    : 'text-gray-400'
            ]" />

        </router-link>
        <CircleAvatar :url="userStorage.user?.avatar?.url ?? RANDOM_AVATAR" size="size-8" id="openModal" :onClick="() => goPage('setting')" />
        <!-- <ThemeToggle /> -->

    </nav>

    <Modal trigger-id="openModal" :title="t(pageModal)" :go-back="() => goPage('setting')">
        <transition name="slide" mode="out-in">
            <KeepAlive>
                <component :is="pages[pageModal]" :key="pageModal" :goPage="goPage" />
            </KeepAlive>
        </transition>
    </Modal>

</template>

<script setup lang="ts">
import { RANDOM_AVATAR, ROUTE } from '@/utils/constant';
import CircleAvatar from '../Avatar/CircleAvatar.vue';
import { useRoute } from 'vue-router';
import { useDevice } from '@/composables/useDevice';
import { computed, ref } from 'vue';
import Modal from '../Modal/Modal.vue';
import SettingsUI from './components/SettingsUI.vue';
import { useTranslate } from '@/composables/useTranslate';
import { SettingPageType } from '@/types/common';
import ProfileUI from './components/ProfileUI.vue';
import { useUserStore } from '@/stores/user.storage';

const { isMobile } = useDevice()
const route = useRoute()
const { t } = useTranslate()

const userStorage = useUserStore()
const pageModal = ref<SettingPageType>("setting")

const pages = {
    setting: SettingsUI,
    profile: ProfileUI
}

const items = [
    {
        icon: 'fa-solid fa-comment',
        to: ROUTE.CHATS
    },
    {
        icon: 'fa-solid fa-earth-americas',
        to: ROUTE.LANGUAGES,
        hideOnMobile: false
    },
]

const visibleItems = computed(() =>
    items.filter(i => !(isMobile.value && i.hideOnMobile))
)

const goPage = (page: SettingPageType) => {
    pageModal.value = page
}
</script>