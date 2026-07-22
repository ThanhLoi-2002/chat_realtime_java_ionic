<template>
    <aside :class="[
        oaStyle.border.primary,
        oaStyle.bg.primary,
        sysStorage.showSidebar ? 'w-20' : 'w-64',
        'border-r p-4 flex flex-col min-h-screen transition-all duration-300 ease-in-out relative select-none'
    ]">

        <div class="space-y-1 flex-1 overflow-y-auto no-scrollbar mt-2 mb-4">
            <div v-for="(item, index) in currentMenus" :key="index" class="w-full">

                <div v-if="countChildPage(item)">
                    <button @click="toggleSubMenu(item.code)" :class="[
                        isParentActive(item)
                            ? 'text-blue-600 dark:text-blue-400 font-semibold'
                            : [oaStyle.bg.hoverBlue, 'text-gray-700 dark:text-gray-300'],
                        'w-full flex items-center px-3 py-2.5 rounded-lg text-sm font-medium transition-colors cursor-pointer justify-start'
                    ]">
                        <div class="flex items-center gap-3 justify-start flex-1 min-w-0">
                            <div v-html="item.icon" class="'text-base w-5 text-center shrink-0'"></div>
                            <span :class="[
                                'whitespace-nowrap transition-all duration-300 ease-in-out block-text',
                                sysStorage.showSidebar ? 'opacity-0 max-w-0 pointer-events-none' : 'opacity-100 max-w-37.5'
                            ]">{{ item.code }}</span>
                        </div>

                        <i :class="[
                            openMenus.includes(item.code) ? 'fa-solid fa-chevron-up' : 'fa-solid fa-chevron-down',
                            'text-xs transition-all duration-300 text-gray-400 shrink-0',
                            sysStorage.showSidebar ? 'opacity-0 max-w-0 scale-0' : 'opacity-100 max-w-37.5'
                        ]" />
                    </button>

                    <div v-show="openMenus.includes(item.code) && !sysStorage.showSidebar"
                        class="mt-1 pl-8 space-y-1 overflow-hidden transition-all duration-300 ease-in-out">
                        <router-link v-for="sub in item.children" :key="sub.code" :to="sub.path" v-slot="{ }" custom>
                            <button @click="$router.push(sub.path)" :class="[
                                // Logic kiểm tra ranh giới: Khớp chính xác hoặc là thư mục con
                                ($route.path === item.path || $route.path.startsWith(item.path + '/'))
                                    ? 'bg-blue-50 text-blue-600 dark:bg-blue-950/50 dark:text-blue-400 font-semibold'
                                    : [oaStyle.bg.hoverBlue, 'text-gray-700 dark:text-gray-300'],
                                'flex gap-3 w-full text-left px-3 py-2 rounded-md text-sm transition-colors whitespace-nowrap cursor-pointer'
                            ]">
                                <div v-html="sub.icon" class="'text-base w-5 text-center shrink-0'"></div>
                                <p>{{ sub.code }}</p>
                            </button>
                        </router-link>
                    </div>
                </div>

                <router-link v-else :to="item.path || ''" v-slot="{ navigate }" custom>
                    <button @click="navigate" :class="[
                        // Logic kiểm tra ranh giới: Khớp chính xác hoặc là thư mục con
                        ($route.path === item.path || $route.path.startsWith(item.path + '/'))
                            ? 'bg-blue-50 text-blue-600 dark:bg-blue-950/50 dark:text-blue-400 font-semibold'
                            : [oaStyle.bg.hoverBlue, 'text-gray-700 dark:text-gray-300'],
                        'w-full flex items-center px-3 py-2.5 rounded-lg text-sm font-medium transition-colors cursor-pointer justify-start'
                    ]">
                        <p v-html="item.icon" :class="['text-base w-5 text-center shrink-0 mr-3']"></p>
                        <span :class="[
                            'whitespace-nowrap transition-all duration-300 ease-in-out block-text',
                            sysStorage.showSidebar ? 'opacity-0 max-w-0 pointer-events-none' : 'opacity-100 max-w-37.5'
                        ]">{{ item.code }}</span>
                    </button>
                </router-link>
            </div>

            <button @click="logout"
                class="w-full flex items-center gap-3 px-3 py-2 rounded-md hover:bg-gray-100 dark:hover:bg-gray-700 transition text-red-600 dark:text-red-400 cursor-pointer">
                <i class="fas fa-sign-out-alt shrink-0"></i>
                <span :class="[
                    sysStorage.showSidebar ? 'opacity-0 max-w-0 pointer-events-none' : 'opacity-100 max-w-37.5',
                    'whitespace-nowrap transition-all duration-300 ease-in-out block-text'
                ]">{{ t("logout") }}</span>
            </button>

        </div>


    </aside>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import { oaStyle } from '@/assets/tailwindcss';
import { useSystemStore } from '@/stores/system.storage';
import { useAuth } from '@/composables/useAuth';
import { useTranslate } from '@/composables/useTranslate';
import { useMenuStore } from '@/stores/menu.storage';
import { StructureType } from '@/types/entities';
import { MenuTypeEnum } from '@/types/enum';

const route = useRoute();
const openMenus = ref<string[]>([]);
const sysStorage = useSystemStore()
const { logout } = useAuth()
const { t } = useTranslate()
const menuStor = useMenuStore()

const currentMenus = computed(() => {
    const segments = route.path.split('/').filter(Boolean)

    // admin/system/user
    // => ['admin','system','user']

    const currentParent = segments[1] // system

    return menuStor.menuList.find(
        item => item.code === currentParent
    )?.children || []
})


const toggleSubMenu = (menuName: string) => {
    const index = openMenus.value.indexOf(menuName);
    if (index > -1) {
        openMenus.value.splice(index, 1);
    } else {
        openMenus.value.push(menuName);
    }
};

const isParentActive = (item: StructureType): boolean => {
    if (!item.children) return false;
    return item.children.some(sub => route.path === sub.path);
};

const countChildPage = (list: StructureType) => {
    if (list.children.length > 0) {
        let count = 0
        list.children.forEach(i => i.menuType == MenuTypeEnum.PAGE && count++)

        return count >= 2
    } else return false
}
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