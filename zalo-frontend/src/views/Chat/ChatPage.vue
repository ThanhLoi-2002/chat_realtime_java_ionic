<script setup lang="ts">
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import BaseButton from '@/components/Button/BaseButton.vue';
import { useTranslate } from '@/composables/useTranslate';
import { RANDOM_AVATAR } from '@/utils/constant';
import ConversationSection from './ConversationSection.vue';
import { useConversationStore } from '@/stores/conversation.storage';
import Modal from '@/components/Modal/Modal.vue';
import FriendProfileUI from './component/FriendProfileUI.vue';
import { ref } from 'vue';

const { t } = useTranslate()

const conversationStorage = useConversationStore()
const isShowInfoSection = ref(false)
</script>

<template>

  <div class="flex h-screen bg-white dark:bg-gray-800">
    <!-- CHAT LIST -->
    <section :class="[
      'flex-1 sm:flex-none w-88 max-w-160 border-r border-slate-200 dark:border-slate-500 flex flex-col',
      conversationStorage.conversation ? 'hidden sm:flex' : 'flex'
    ]">

      <conversation-section />

    </section>

    <div v-if="!conversationStorage.conversation"
      class="hidden sm:flex flex-1 items-center justify-center text-gray-400">
      Select a conversation
    </div>

    <!-- CHAT AREA -->
    <main v-else :class="[
      'flex-1 flex flex-col bg-gray-100 dark:bg-gray-900',
      conversationStorage.conversation ? 'flex' : 'hidden sm:flex'
    ]">

      <!-- HEADER -->
      <header class="h-16 flex items-center justify-between px-6
          bg-white dark:bg-gray-900
          border-b border-gray-200 dark:border-slate-500">

        <div class="flex items-center justify-between gap-3 w-full">
          <div class="flex items-cente gap-3">
            <!-- Back button (mobile only) -->
            <button class="sm:hidden text-gray-600 dark:text-white" @click="conversationStorage.selectConversation()">
              <i class="fas fa-arrow-circle-left"></i>
            </button>
            <circle-avatar :url="RANDOM_AVATAR" size="size-10" :onClick="() => { }" id="friendProfile" />

            <div class="flex flex-col">
              <span class="font-medium dark:text-slate-200">
                Alice Johnson
              </span>
              <span class="text-xs bg-green-400 rounded-full w-2.5 h-2.5">
              </span>
            </div>
          </div>

          <div>
            <i class="fas fa-toggle-off text-2xl cursor-pointer" v-if="!isShowInfoSection"
              @click="isShowInfoSection = !isShowInfoSection"></i>
            <i class="fas fa-toggle-on text-2xl text-blue-500 cursor-pointer" v-if="isShowInfoSection"
              @click="isShowInfoSection = !isShowInfoSection"></i>
          </div>
        </div>

        <modal trigger-id="friendProfile" :title="t('profile')">
          <friend-profile-u-i :user="conversationStorage.friend" />
        </modal>
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

    </main>

    <!-- INFO SECTION -->
    <!-- <aside v-if="isShowInfoSection"
    class="fixed top-0 right-0 h-full w-80 bg-white dark:bg-gray-900 z-50"> -->
    <aside :class="[
      'w-80 border-l border-slate-200 dark:border-slate-500 bg-white dark:bg-gray-900 flex flex-col transition-all duration-300',
      isShowInfoSection ? 'flex' : 'hidden'
    ]">
      <div class="p-4 font-semibold dark:text-white">
        Info
      </div>

      <div class="p-4">
        <!-- ví dụ -->
        <circle-avatar :url="RANDOM_AVATAR" size="size-16" :onClick="() => { }" />

        <div class="mt-3 dark:text-slate-200">
          {{ conversationStorage.friend?.username }}
        </div>
      </div>
    </aside>

  </div>
</template>