<script setup lang="ts">
import { useTranslate } from "@/composables/useTranslate";
import { FriendMenuType } from "@/types/common";

const props = defineProps({
    activeMenu: String
})

const emit = defineEmits(["changeTab"])

const { t } = useTranslate()

const items = [
    {
        title: 'friendList',
        icon: 'fa-solid fa-users',
    },
    {
        title: 'groupList',
        icon: 'fa-solid fa-people-group',
    },
    {
        title: 'friendInvitation',
        icon: 'fa-solid fa-person-circle-plus',
    },
    {
        title: 'groupInvitation',
        icon: 'fa-solid fa-users-viewfinder',
    },
]

const changeTab = (value: FriendMenuType | any) => {
    emit("changeTab", value)
}
</script>

<template>
    <div class="flex-1 px-2 py-3 space-y-1 text-sm bg-white dark:bg-[#1f2937] h-full">

        <!-- ITEM -->
        <div v-for="(item, index) in items" :key="index" @click="changeTab(item.title)" :class="[
            'px-3 py-2 rounded-sm cursor-pointer flex items-center gap-2 transition-all duration-200',
            'text-gray-700 dark:text-gray-300',
            activeMenu === item.title
                ? 'bg-blue-600 text-white font-medium'
                : 'hover:bg-gray-100 dark:hover:bg-gray-700'
        ]">
            <i :class="item.icon" />
            <span>{{ t(item.title) }}</span>
        </div>

    </div>
</template>