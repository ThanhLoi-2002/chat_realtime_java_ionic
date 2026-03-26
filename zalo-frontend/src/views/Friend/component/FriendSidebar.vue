<script setup lang="ts">
import { useTranslate } from "@/composables/useTranslate";
import { FriendshipStatusEnum } from "@/types/enum";
import { ref } from "vue"

type MenuType = "friends" | "groups" | "friendInvitations" | "groupInvitations"
const activeMenu = ref<MenuType>("friends")
const { t } = useTranslate()

const items = [
    {
        title: 'friendList',
        icon: 'fa-solid fa-users',
        value: "friends",
        onClick: (value: FriendshipStatusEnum) => {}
    },
    {
        title: 'groupList',
        icon: 'fa-solid fa-people-group',
        value: 'groups',
        onClick: (value: FriendshipStatusEnum) => {}
    },
    {
        title: 'addFriendRequest',
        icon: 'fa-solid fa-person-circle-plus',
        value: 'friendInvitations',
        onClick: (value: FriendshipStatusEnum) => {}
    },
    {
        title: 'groupInvation',
        icon: 'fa-solid fa-users-viewfinder',
        value: 'groupInvitations',
        onClick: (value: FriendshipStatusEnum) => {}
    },
]

const changeTab = (value: MenuType | any) => {
    activeMenu.value = value
}
</script>

<template>
    <div class="flex-1 px-2 py-3 space-y-1 text-sm">

        <!-- ITEM -->
        <div v-for="(item, index) in items" :key="index" @click="changeTab(item.value)" :class="[
            'px-3 py-2 rounded-sm cursor-pointer flex items-center gap-2 transition-all duration-200',
            'text-gray-700 dark:text-gray-300',
            activeMenu === item.value
                ? 'bg-blue-600 text-white font-medium'
                : 'hover:bg-gray-100 dark:hover:bg-gray-800'
        ]">
            <i :class="item.icon"/>
            <span>{{ t(item.title) }}</span>
        </div>

    </div>
</template>