<template>
  <div class="p-4 dark:text-white flex flex-col gap-3">
    <div class="flex justify-between gap-2">
      <span class="text-xl font-light">
        {{ t('message') }}
      </span>
      <div class="flex gap-2">
        <ion-button @click="goScan">
          <i class="fas fa-qrcode"></i>
        </ion-button>
        <ion-button :onClick="() => openModal('addFriend')">
          <i class="fas fa-user-plus"></i>
        </ion-button>
        <ion-button :onClick="() => openModal('createGroup')">
          <i class="fa-solid fa-users-line"></i>
        </ion-button>
      </div>
    </div>

    <modal ref="modalRef" :title="t((pageModal == 'friendProfile') ? 'profile' : pageModal)"
      :go-back="() => goPage('addFriend')" :isDisplayBackButton="pageModal != Object.keys(pages)[0]">
      <transition name="slide" mode="out-in">
        <!-- <KeepAlive> -->
        <component :is="pages[pageModal]" :key="pageModal" :goPage="goPage" :setUser="(u: UserType) => selectedUser = u"
          :user="selectedUser" />
        <!-- </KeepAlive> -->
      </transition>
    </modal>

    <input :placeholder="t('search') + '...'" class="w-full px-4 py-2 rounded-lg
         bg-white text-gray-800
         dark:bg-gray-800 dark:text-gray-200
         placeholder-gray-400 dark:placeholder-gray-500
         border border-gray-200 dark:border-gray-700
         focus:outline-none focus:ring-1 focus:ring-blue-400" />
  </div>

  <div ref="conversationRef" class="flex-1 overflow-y-auto max-h-[73%] sm:max-h-full" @scroll="scrollMore">
    <conversation-u-i v-for="item in conversationStorage.conversations" :key="item.id" @click="selectConversation(item)"
      :conversation="item" :selectedConversation="conversationStorage.conversation" />
  </div>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { defineAsyncComponent, onMounted, ref } from 'vue';
import ConversationUI from './component/ConversationUI.vue';
import { useConversationStore } from '@/stores/conversation.storage';
import { SearchFriendPageType } from '@/types/common';
import Modal from '@/components/Modal/Modal.vue';
import { ConversationType, UserType } from '@/types/entities';
import { useScroll } from '@/composables/useScroll';
import { useRouter } from 'vue-router';
import { useDevice } from '@/composables/useDevice';

const CreateGroupUI = defineAsyncComponent(() => import('./component/CreateGroupUI.vue'));
const AddFriendUI = defineAsyncComponent(() => import('./component/AddFriendUI.vue'));
const FriendProfileUI = defineAsyncComponent(() => import('./component/FriendProfileUI.vue'));

const { t } = useTranslate()
const { onScroll } = useScroll()

const conversationStorage = useConversationStore()
const selectedUser = ref<UserType | null>(null)
const modalRef = ref()
const router = useRouter()

const pageModal = ref<SearchFriendPageType | "createGroup">("addFriend")
const pages = {
  addFriend: AddFriendUI,
  friendProfile: FriendProfileUI,
  createGroup: CreateGroupUI
}

const conversationRef = ref<HTMLElement | null>(null)

const scrollMore = () => {
  if (conversationStorage.isLoading || !conversationStorage.hasMore) return
  onScroll(conversationRef.value, () =>
    conversationStorage.getConversations()
  )
}

const goScan = () => {
  router.push('/scan')
}

const goPage = (page: SearchFriendPageType | "createGroup") => {
  pageModal.value = page
}

const selectConversation = (item: ConversationType) => {
  conversationStorage.selectConversation(item)
}

const openModal = (page: SearchFriendPageType | "createGroup") => {
  goPage(page)
  modalRef?.value.present()
}

onMounted(() => {
  conversationStorage.getConversations()
})
</script>