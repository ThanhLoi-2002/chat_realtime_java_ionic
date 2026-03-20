<template>
    <!-- HEADER -->
    <header class="h-16 flex items-center justify-between px-6
          bg-white dark:bg-gray-900
          border-b border-gray-200 dark:border-slate-500">

        <div class="flex items-center justify-between gap-3 w-full">
            <div class="flex items-cente gap-3">
                <!-- Back button (mobile only) -->
                <MobileBackButton :onClick="onBack" />
                <circle-avatar :url="RANDOM_AVATAR" size="size-10" :onClick="() => friendProfileModal?.present()" />

                <div class="flex flex-col">
                    <span class="font-medium dark:text-slate-200">
                        Alice Johnson
                    </span>
                    <span class="text-xs bg-green-400 rounded-full w-2.5 h-2.5">
                    </span>
                </div>
            </div>

            <div>
                <i class="fas fa-toggle-off text-2xl cursor-pointer dark:text-white" v-if="!isShowInfoSection"
                    @click="emit('update:isShowInfoSection', !isShowInfoSection)"></i>
                <i class="fas fa-toggle-on text-2xl text-blue-500 cursor-pointer" v-if="isShowInfoSection"
                    @click="emit('update:isShowInfoSection', !isShowInfoSection)"></i>
            </div>
        </div>

        <Modal ref="friendProfileModal" :title="t('profile')">
            <friend-profile-u-i :user="conversationStorage.friend" />
        </Modal>
    </header>

    <!-- MESSAGES -->
    <div class="flex-1 overflow-y-auto p-6 space-y-4">
        <!-- received -->
        <div class="flex gap-2 items-center">
            <circle-avatar :url="RANDOM_AVATAR" size="size-8" :onClick="() => { }" />
            <div
                class="bg-white dark:bg-gray-800 dark:text-slate-100 px-4 py-2 rounded-xl text-sm border border-slate-300">
                Hi! Did you read the brief?
            </div>
        </div>

        <!-- sent -->
        <div class="flex items-end gap-2 flex-row-reverse">
            <div
                class="bg-white dark:bg-gray-800 dark:text-slate-100 px-4 py-2 rounded-xl text-sm border border-slate-300">
                Yes, looks good!
            </div>
        </div>
    </div>

    <!-- INPUT -->
    <footer class="p-4 border-t
          border-gray-200 dark:border-slate-500
          bg-white dark:bg-gray-900">

        <div class="flex gap-3">
            <input :placeholder="t('typeMessage')" class="flex-1 px-4 py-1.5 rounded-lg
                bg-gray-100 dark:bg-gray-800 dark:text-slate-200" />

            <base-button icon="fa-solid fa-paper-plane text-blue-500" />
        </div>

    </footer>
</template>
<script setup lang="ts">
import MobileBackButton from '@/components/Button/MobileBackButton.vue';
import Modal from '@/components/Modal/Modal.vue';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { useSystemStore } from '@/stores/system.storage';
import { RANDOM_AVATAR } from '@/utils/constant';
import { ref } from 'vue';
import FriendProfileUI from './component/FriendProfileUI.vue';

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const friendProfileModal = ref()

const conversationStorage = useConversationStore()
const systemStorage = useSystemStore()
const { t } = useTranslate()

const emit = defineEmits(['update:isShowInfoSection'])

const onBack = () => {
    conversationStorage.selectConversation()
    systemStorage.setShowBottomMenu(true)
}
</script>