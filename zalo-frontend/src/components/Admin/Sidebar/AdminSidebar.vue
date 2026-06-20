<template>
    <aside :class="[
        oaStyle.border.primary,
        oaStyle.bg.primary,
        sysStorage.showSidebar ? 'w-20' : 'w-64',
        'border-r p-4 hidden md:flex flex-col min-h-screen transition-all duration-300 ease-in-out relative select-none'
    ]">
        <div
            :class="[oaStyle.text.primary, oaStyle.border.primary, 'flex items-center cursor-pointer px-2 pb-2 h-10 border-b overflow-hidden']">
            <circle-avatar size="size-8 shrink-0" />
            <span :class="[
                'text-sm font-bold pl-3 transition-all duration-300 ease-in-out whitespace-nowrap block-text',
                sysStorage.showSidebar ? 'opacity-0 max-w-0 pointer-events-none' : 'opacity-100 max-w-37.5'
            ]">SVoucher</span>
        </div>

        <div class="space-y-1 flex-1 overflow-y-auto no-scrollbar mt-2">
            <div v-for="(item, index) in menuItems" :key="index" class="w-full">

                <div v-if="item.items && item.items.length > 0">
                    <button @click="toggleSubMenu(item.name)" :class="[
                        isParentActive(item)
                            ? 'text-blue-600 dark:text-blue-400 font-semibold'
                            : [oaStyle.bg.hoverBlue, 'text-gray-700 dark:text-gray-300'],
                        'w-full flex items-center px-3 py-2.5 rounded-lg text-sm font-medium transition-colors cursor-pointer justify-start'
                    ]">
                        <div class="flex items-center justify-start flex-1 min-w-0">
                            <i :class="[item.icon, 'text-base w-5 text-center shrink-0 mr-3']" />
                            <span :class="[
                                'whitespace-nowrap transition-all duration-300 ease-in-out block-text',
                                sysStorage.showSidebar ? 'opacity-0 max-w-0 pointer-events-none' : 'opacity-100 max-w-37.5'
                            ]">{{ item.name }}</span>
                        </div>

                        <i :class="[
                            openMenus.includes(item.name) ? 'fa-solid fa-chevron-up' : 'fa-solid fa-chevron-down',
                            'text-xs transition-all duration-300 text-gray-400 shrink-0',
                            sysStorage.showSidebar ? 'opacity-0 max-w-0 scale-0' : 'opacity-100 max-w-37.5'
                        ]" />
                    </button>

                    <div v-show="openMenus.includes(item.name) && !sysStorage.showSidebar"
                        class="mt-1 pl-8 space-y-1 overflow-hidden transition-all duration-300 ease-in-out">
                        <router-link v-for="sub in item.items" :key="sub.name" :to="sub.href" v-slot="{ isActive }"
                            custom>
                            <button @click="$router.push(sub.href)" :class="[
                                isActive
                                    ? 'bg-blue-50 text-blue-600 dark:bg-blue-950/50 dark:text-blue-400 font-semibold'
                                    : [oaStyle.bg.hoverBlue, 'text-gray-700 dark:text-gray-300'],
                                'w-full text-left px-3 py-2 rounded-md text-sm transition-colors block whitespace-nowrap cursor-pointer'
                            ]">
                                {{ sub.name }}
                            </button>
                        </router-link>
                    </div>
                </div>

                <router-link v-else :to="item.href || ''" v-slot="{ isActive }" custom>
                    <button @click="item.href && $router.push(item.href)" :class="[
                        isActive
                            ? 'bg-blue-50 text-blue-600 dark:bg-blue-950/50 dark:text-blue-400 font-semibold'
                            : [oaStyle.bg.hoverBlue, 'text-gray-700 dark:text-gray-300'],
                        'w-full flex items-center px-3 py-2.5 rounded-lg text-sm font-medium transition-colors cursor-pointer justify-start'
                    ]">
                        <i :class="[item.icon, 'text-base w-5 text-center shrink-0 mr-3']" />
                        <span :class="[
                            'whitespace-nowrap transition-all duration-300 ease-in-out block-text',
                            sysStorage.showSidebar ? 'opacity-0 max-w-0 pointer-events-none' : 'opacity-100 max-w-37.5'
                        ]">{{ item.name }}</span>
                    </button>
                </router-link>

            </div>
        </div>

    </aside>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import { oaStyle } from '@/assets/tailwindcss';
import { Menu } from '@/types/common';
import { useSystemStore } from '@/stores/system.storage';

const route = useRoute();
const openMenus = ref<string[]>([]);
const sysStorage = useSystemStore()

const menuItems = ref<Menu[]>([
    { name: 'Tổng quan', icon: 'fa-solid fa-house', href: '/oa/dashboard' },
    { name: 'Người dùng', icon: 'fa-solid fa-user', href: '/oa/users' },
    { name: 'Kịch bản tin nhắn', icon: 'fa-solid fa-folder', href: '/oa/scripts' },
    {
        name: 'Từ khóa',
        icon: 'fa-solid fa-sliders',
        items: [
            { name: 'Từ khóa cơ bản', href: '/oa/keywords/basic' },
            { name: 'Hỏi đáp AI', href: '/oa/keywords/ai' }
        ]
    },
    { name: 'Tin nhắn chào mừng', icon: 'fa-solid fa-comment-dots', href: '/oa/welcome' },
    { name: 'userRole', icon: 'fa-solid fa-comment-dots', href: '/admin/user-role' },
]);

const toggleSubMenu = (menuName: string) => {
    const index = openMenus.value.indexOf(menuName);
    if (index > -1) {
        openMenus.value.splice(index, 1);
    } else {
        openMenus.value.push(menuName);
    }
};

const isParentActive = (item: Menu): boolean => {
    if (!item.items) return false;
    return item.items.some(sub => route.path === sub.href);
};
</script>

<style scoped>
/* Class bổ trợ giúp hiệu ứng chữ biến mất mượt theo chiều ngang */
.block-text {
    overflow: hidden;
    display: inline-block;
}

/* Ẩn scrollbar xấu xí của hệ thống nếu menu quá dài */
.no-scrollbar::-webkit-scrollbar {
    width: 0px;
}
</style>