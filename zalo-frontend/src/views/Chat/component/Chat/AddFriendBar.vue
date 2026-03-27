<template>
    <div class="absolute top-1 left-1/2 -translate-x-1/2
         px-4 py-2 flex gap-2 justify-between
         bg-gray-100 dark:bg-slate-700 border
         border-slate-300 dark:border-slate-800
         z-10 rounded-md shadow min-w-[98%]
         text-xs md:text-base" :class="[style.text.primary]">
        <div class="flex gap-2 items-center">
            <i class="fa-solid fa-user-plus"></i>
            <span :class="[style.text.primary, 'text-sm']">{{ t('sendFriendRequest') }}</span>
        </div>
        <div class="flex gap-4 items-center">
            <div class="px-2 py-1 text-sm rounded-sm cursor-pointer bg-slate-300 dark:bg-slate-600"
                :class="[style.text.primary]" @click="openModal">{{ t("addFriend") }}</div>
            <i class="fa-solid fa-ellipsis cursor-pointer"></i>
        </div>
    </div>

    <Modal ref="modalRef" :title="t(pageModal == 'friendProfile' ? 'profile' : pageModal)" :go-back="() => goPage('addFriend')"
        :isDisplayBackButton="pageModal != Object.keys(pages)[0]">
        <transition name="slide" mode="out-in">
            <KeepAlive>
                <component :is="pages[pageModal]" :key="pageModal" :goPage="goPage" :user="user" />
            </KeepAlive>
        </transition>
    </Modal>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import Modal from '@/components/Modal/Modal.vue';
import { useTranslate } from '@/composables/useTranslate';
import AddFriendRequestUI from '@/views/Friend/component/AddFriendRequestUI.vue';
import { computed, ref } from 'vue';
import FriendProfileUI from '../FriendProfileUI.vue';
import { useConversation } from '@/composables/useConversation';
import { useConversationStore } from '@/stores/conversation.storage';

const { t } = useTranslate()
const { getRecipient } = useConversation()
const conversationStorage = useConversationStore()

const pageModal = ref<"addFriend" | "friendProfile">("addFriend")
const modalRef = ref()

const pages = {
    addFriend: AddFriendRequestUI,
    friendProfile: FriendProfileUI
}

const goPage = (page: "addFriend" | "friendProfile" | any) => {
    pageModal.value = page
}

const openModal = () => {
    goPage('addFriend')
    modalRef?.value.present()
}

const user = computed(() => {
    return getRecipient(conversationStorage.conversation)
})
</script>