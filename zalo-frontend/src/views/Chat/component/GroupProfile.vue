<template>
    <div class="bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100 h-[90%] flex flex-col rounded-md">

        <!-- BODY -->
        <div class="flex-1 overflow-y-auto overflow-x-hidden pb-10">

            <!-- GROUP INFO -->
            <div class="flex flex-col items-center py-4 gap-1">
                <div class="flex w-full gap-4 ml-8">
                    <div class="relative">
                        <div
                            class="w-20 h-20 rounded-full bg-gray-200 dark:bg-gray-700 flex items-center justify-center">
                            <i class="fas fa-users text-3xl text-gray-500 dark:text-gray-300"></i>
                        </div>

                        <!-- camera icon -->
                        <div class="absolute bottom-0 right-0 bg-gray-300 dark:bg-gray-800 p-1 rounded-full">
                            <i class="fas fa-camera text-xs text-gray-700 dark:text-white"></i>
                        </div>
                    </div>

                    <div class="flex items-center gap-2 mt-3">
                        <span class="font-medium truncate">JHV - Digitalization IT</span>
                        <i class="fas fa-pen text-sm text-gray-500 dark:text-gray-400 cursor-pointer"></i>
                    </div>
                </div>

                <button
                    class="mt-3 bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 px-6 py-2 rounded-sm text-sm cursor-pointer w-[80%] transition"
                    @click="goToMessage">
                    {{ t("message") }}
                </button>
            </div>

            <!-- MEMBERS -->
            <div class="border-t border-gray-200 dark:border-gray-800 px-4 py-3">
                <div class="text-sm mb-2 text-gray-600 dark:text-gray-300">Thành viên (5)</div>

                <div class="flex items-center -space-x-3">
                    <img v-for="user in conversation.members" :key="user.id" :src="user.avatar.url ?? RANDOM_AVATAR"
                        class="w-10 h-10 rounded-full border-2 border-white dark:border-gray-900 cursor-pointer" />

                    <!-- more -->
                    <div
                        class="w-10 h-10 flex items-center justify-center rounded-full bg-gray-300 dark:bg-gray-700 border-2 border-white dark:border-gray-900 text-gray-700 dark:text-white">
                        ...
                    </div>
                </div>
            </div>

            <!-- MEDIA -->
            <div
                class="border-t border-gray-200 dark:border-gray-800 px-4 py-6 text-center text-gray-500 dark:text-gray-400 text-sm">
                Chưa có ảnh nào được<br />
                chia sẻ trong nhóm này
            </div>

            <!-- LINK -->
            <div class="border-t border-gray-200 dark:border-gray-800 px-4 py-3">
                <div class="text-sm mb-2 text-gray-600 dark:text-gray-300">Link tham gia nhóm</div>

                <div class="flex items-center justify-between bg-gray-100 dark:bg-gray-800 p-2 rounded-md">
                    <span class="text-blue-500 dark:text-blue-400 text-sm truncate">
                        https://zalo.me/g/worxuj745
                    </span>

                    <div class="flex gap-2 text-gray-600 dark:text-gray-300">
                        <i class="fas fa-copy cursor-pointer"></i>
                        <i class="fas fa-share cursor-pointer"></i>
                    </div>
                </div>
            </div>

            <!-- ACTIONS -->
            <div class="border-t border-gray-200 dark:border-gray-800">
                <div class="px-4 py-3 flex items-center gap-3 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800">
                    <i class="fas fa-cog"></i>
                    <span>Quản lý nhóm</span>
                </div>

                <div
                    class="px-4 py-3 flex items-center gap-3 cursor-pointer text-red-500 hover:bg-gray-100 dark:hover:bg-gray-800">
                    <i class="fas fa-sign-out-alt"></i>
                    <span>Rời nhóm</span>
                </div>
            </div>

        </div>
    </div>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { ConversationType } from '@/types/entities';
import { RANDOM_AVATAR, ROUTE } from '@/utils/constant';
import { inject } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps<{
    conversation: ConversationType;
}>();


const { t } = useTranslate()
const router = useRouter()
const convStorage = useConversationStore()

const dismiss = inject<() => void>("modalDismiss")

const goToMessage = () => {
    convStorage.selectConversation(props.conversation)
    router.push(ROUTE.CHATS)
    dismiss?.()
}
</script>