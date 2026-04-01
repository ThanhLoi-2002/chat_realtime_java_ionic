<template>

        <div
            class="bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100 h-[90%] flex flex-col rounded-md">

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
                            <span class="font-medium truncate">{{ convStorage.conversation?.name }}</span>
                            <i class="fas fa-pen text-sm text-gray-500 dark:text-gray-400 cursor-pointer"></i>
                        </div>
                    </div>

                    <button
                        class="mt-3 bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 px-6 py-2 rounded-sm text-sm cursor-pointer w-[90%] transition"
                        @click="goToMessage">
                        {{ t("message") }}
                    </button>
                </div>

                <!-- MEMBERS -->
                <div class="border-t border-gray-200 dark:border-gray-800 px-4 py-3">
                    <div class="text-sm mb-2 text-gray-600 dark:text-gray-300">{{ t("member") }} ({{
                        conversation.members?.length }})</div>

                    <div class="flex items-center -space-x-4">
                        <div class="flex -space-x-4">
                            <div v-for="user in conversation.members" :key="user.id" class="relative group">
                                <!-- AVATAR -->
                                <CircleAvatar :user="user" size="size-10" />

                                <!-- TOOLTIP -->
                                <div class="absolute bottom-full left-1/2 -translate-x-1/2 mb-2
                                px-2 py-1 text-xs rounded-md shadow-lg
                                bg-gray-900 text-white 
                                dark:bg-white dark:text-black
                                opacity-0 scale-90
                                group-hover:opacity-100 group-hover:scale-100
                                transition-all duration-200
                                whitespace-nowrap z-50 pointer-events-none">
                                    {{ user.username }}
                                </div>
                            </div>
                        </div>

                        <!-- more -->
                        <div class="z-2 w-10 h-10 flex items-center justify-center rounded-full bg-gray-300 dark:bg-gray-700 border-2 border-white dark:border-gray-900 text-gray-700 dark:text-white cursor-pointer"
                            @click="() => emit('update:view', 'member')">
                            <i class="fas fa-ellipsis-h"></i>
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
                    <div
                        class="px-4 py-3 flex items-center gap-3 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800">
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
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { ConversationType } from '@/types/entities';
import { ROUTE } from '@/utils/constant';
import { inject, ref } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps<{
    conversation: ConversationType;
}>();

const { t } = useTranslate()
const router = useRouter()
const convStorage = useConversationStore()

const dismiss = inject<() => void>("modalDismiss")

const emit = defineEmits(['update:view'])

const goToMessage = () => {
    convStorage.selectConversation(props.conversation)
    router.push(ROUTE.CHATS)
    dismiss?.()
}
</script>