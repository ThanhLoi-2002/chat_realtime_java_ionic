<template>
  <div class="p-4 dark:text-white flex flex-col gap-3">
    <div class="flex justify-between gap-2">
      <span class="text-xl font-light">
        {{ t('message') }}
      </span>
      <div class="flex gap-2">
        <ion-button id="addFriend" :onClick="() => goPage('addFriend')">
          <i class="fas fa-user-plus"></i>
        </ion-button>
        <ion-button id="createGroup" :onClick="() => goPage('createGroup')">
          <i class="fa-solid fa-users-line"></i>
        </ion-button>
      </div>
    </div>

    <modal trigger-id="addFriend" :title="t((pageModal == 'friendProfile') ? 'profile' : pageModal)"
      :go-back="() => goPage('addFriend')">
      <transition name="slide" mode="out-in">
        <!-- <KeepAlive> -->
        <component :is="pages[pageModal]" :key="pageModal" :goPage="goPage" :setUser="(u: UserType) => selectedUser = u"
          :user="selectedUser" />
        <!-- </KeepAlive> -->
      </transition>
    </modal>

    <modal trigger-id="createGroup" :title="t(pageModal)"
      :go-back="() => goPage('createGroup')">
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

  <div class="flex-1 overflow-y-auto">
    <conversation-u-i v-for="item in conversationStorage.conversations" :key="item.id"
      @click="conversationStorage.selectConversation(item)" :conversation="item"
      :selectedConversation="conversationStorage.conversation" />
  </div>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { ref } from 'vue';
import ConversationUI from './component/ConversationUI.vue';
import { useConversationStore } from '@/stores/conversation.storage';
import { SearchFriendPageType } from '@/types/common';
import AddFriendUI from './component/AddFriendUI.vue';
import FriendProfileUI from './component/FriendProfileUI.vue';
import Modal from '@/components/Modal/Modal.vue';
import { UserType } from '@/types/entities';
import CreateGroupUI from './component/CreateGroupUI.vue';

const { t } = useTranslate()

const conversationStorage = useConversationStore()
const selectedUser = ref<UserType | null>(null)

const pageModal = ref<SearchFriendPageType | "createGroup">("addFriend")
const pages = {
  addFriend: AddFriendUI,
  friendProfile: FriendProfileUI,
  createGroup: CreateGroupUI
}

const goPage = (page: SearchFriendPageType | "createGroup") => {
  pageModal.value = page
}
</script>