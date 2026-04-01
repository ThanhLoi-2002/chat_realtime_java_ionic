<template>
    <ProfileUI v-if="userStorage.user?.id == user?.id" :go-page="() => goPage?.('addFriend')" />
    <div v-else class="h-[85%]">
        <div class="relative">
            <div class="h-36 cursor-pointer">
                <img :src="user?.cover?.url ?? RANDOM_AVATAR" class="w-full h-full rounded-lg" />
            </div>

            <!-- Avatar center -->
            <div class="absolute left-1/2 -bottom-12 -translate-x-1/2">
                <div class="relative w-24 h-24">

                    <!-- Avatar -->
                    <CircleAvatar :user="user"
                        custom-class="w-24 h-24 rounded-full ring-2 ring-white dark:ring-slate-800 overflow-hidden bg-gray-300"
                        :is-disabled="true" />
                </div>
            </div>
        </div>

        <!-- BODY -->
        <div class="pt-10 pb-4 px-2 dark:bg-gray-800 overflow-auto h-[75%]">
            <!-- Name -->
            <div class="flex items-center justify-center gap-2">
                <h2 class="text-lg font-semibold dark:text-slate-300">
                    {{ user?.username }}
                </h2>
                <i class="fa fa-pencil text-sm cursor-pointer 
                        text-gray-500 dark:text-gray-400"></i>
            </div>

            <!-- ACTION BUTTON -->
            <div class="flex gap-3 mt-2">
                <button
                    class="flex-1 border hover:bg-gray-50 dark:bg-gray-700 dark:hover:bg-slate-600 dark:border-slate-700 dark:text-slate-300 py-2 rounded cursor-pointer">
                    {{ t('addFriend') }}
                </button>
                <button class="flex-1 hover:bg-blue-500 bg-blue-600 text-white py-2 rounded cursor-pointer"
                    @click="goToMessage">
                    {{ t('message') }}
                </button>
            </div>

            <!-- INFO -->
            <div class="mt-5 border-t border-gray-700 pt-4">
                <p class="text-gray-500 mb-2 dark:text-slate-200">{{ t('profile') }}</p>

                <div class="flex justify-between text-sm py-1">
                    <span class="text-gray-500 dark:text-slate-400">{{ t("phone") }}</span>
                    <span class="dark:text-slate-300">{{ user?.phone }}</span>
                </div>
            </div>

            <!-- OPTIONS -->
            <div class="mt-5 border-t border-gray-700 pt-4 space-y-1 text-sm">
                <div v-for="(item, index) in items" :key="index" :class="item.class ?? 'dark:text-slate-200'"
                    class="flex items-center gap-3 cursor-pointer hover:bg-slate-200 dark:hover:bg-slate-700 p-2 rounded-sm">
                    <i :class="item.icon"></i>
                    <span>{{ t(item.title) }}</span>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import ProfileUI from '@/components/Sidebar/components/ProfileUI.vue';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { useUserStore } from '@/stores/user.storage';
import { SearchFriendPageType } from '@/types/common';
import { UserType } from '@/types/entities';
import { RANDOM_AVATAR, ROUTE } from '@/utils/constant';
import { inject } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps<{
    user?: UserType | null,
    goPage?: (page: SearchFriendPageType) => void
}>()

const { t } = useTranslate()
const router = useRouter()
const userStorage = useUserStore()
const conversationStorage = useConversationStore()
const dismiss = inject<() => void>("modalDismiss")

const items = [
    {
        icon: 'fa fa-users',
        title: 'Nhóm chung (1)',
    },
    {
        icon: 'fa fa-id-card',
        title: 'Chia sẻ danh thiếp',
    },
    {
        icon: 'fa fa-ban',
        title: 'Chặn tin nhắn và cuộc gọi',
    },
    {
        icon: 'fa fa-warning',
        title: 'Báo xấu',
        class: 'text-red-400'
    },
]

const goToMessage = async () => {
    const success = await conversationStorage.createPrivate(props.user!)
    router.push(ROUTE.CHATS)
    success && dismiss?.()
}
</script>