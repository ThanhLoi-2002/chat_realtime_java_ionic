<template>
    <div class="flex h-screen bg-gray-100 dark:bg-[#1f2937] text-gray-900 dark:text-white">

        <!-- SIDEBAR (DESKTOP) -->
        <div class="hidden md:flex w-64 bg-white dark:bg-[#111827]
                border-r border-gray-200 dark:border-gray-700 flex-col">
            <FriendSidebar :activeMenu="activeMenu" @changeTab="val => activeMenu = val" />
        </div>

        <!-- MOBILE DRAWER -->
        <div v-if="showSidebar" class="fixed inset-0 z-50 flex md:hidden">

            <!-- overlay -->
            <div class="flex-1 bg-black/50" @click="showSidebar = false" />

            <!-- sidebar -->
            <div class="w-64 bg-white dark:bg-[#111827] shadow-xl">
                <FriendSidebar :activeMenu="activeMenu" @changeTab="val => activeMenu = val" />
            </div>
        </div>

        <!-- MAIN -->
        <div class="flex-1 flex flex-col">

            <!-- HEADER -->
            <div
                class="h-14 flex items-center px-4 md:px-6 gap-3 border-b border-gray-200 dark:border-gray-700 bg-white dark:bg-[#1f2937]">
                <!-- menu button -->
                <button class="md:hidden p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition"
                    @click="showSidebar = true">
                    <i class="fa fa-bars text-lg"></i>
                </button>

                <span class="text-base md:text-lg" :class="[style.text.primary]"> {{ t(activeMenu) }} </span>
            </div>

            <FriendListUI v-if="activeMenu === 'friendList'" />

            <div v-else-if="activeMenu === 'groupList'">
                <!-- lời mời kết bạn -->
            </div>

            <FriendInvitationsUI v-else-if="activeMenu === 'friendInvitation'" />

            <div v-else-if="activeMenu === 'groupInvitation'">
                <!-- nhóm -->
            </div>

        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue"
import FriendSidebar from "./component/FriendSidebar.vue"
import { FriendMenuType } from "@/types/common"
import FriendListUI from "./component/FriendListUI.vue"
import { useTranslate } from "@/composables/useTranslate"
import FriendInvitationsUI from "./component/FriendInvitationsUI.vue"
import { style } from "@/assets/tailwindcss"

const { t } = useTranslate()
const showSidebar = ref(false)

const activeMenu = ref<FriendMenuType>("friendList")
</script>