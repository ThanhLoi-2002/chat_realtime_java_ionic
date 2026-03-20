<script setup lang="ts">
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import { useTranslate } from '@/composables/useTranslate';
import { RANDOM_AVATAR } from '@/utils/constant';
import ConversationSection from './ConversationSection.vue';
import { useConversationStore } from '@/stores/conversation.storage';
import { ref } from 'vue';
import ChatContainer from './ChatContainer.vue';
import MobileBackButton from '@/components/Button/MobileBackButton.vue';
import InfoSection from './InfoSection.vue';

const { t } = useTranslate()

const conversationStorage = useConversationStore()
const isShowInfoSection = ref(false)
</script>

<template>

  <div class="flex h-screen bg-white dark:bg-gray-800">
    <!-- CHAT LIST -->
    <section :class="[
      'flex-1 md:flex-none w-88 max-w-3xl border-r border-slate-200 dark:border-slate-500 flex flex-col',
      conversationStorage.conversation ? 'hidden md:flex' : 'flex'
    ]">
      <conversation-section />
    </section>

    <div v-if="!conversationStorage.conversation"
      class="hidden md:flex flex-1 items-center justify-center text-gray-400">
      Select a conversation
    </div>

    <!-- CHAT AREA -->
    <main v-else :class="[
      'flex-1 flex flex-col bg-gray-100 dark:bg-gray-900'
    ]">
    <chat-container v-model:isShowInfoSection="isShowInfoSection"/>

    </main>

    <!-- INFO SECTION -->
    <!-- <aside v-if="isShowInfoSection"
    class="fixed top-0 right-0 h-full w-80 bg-white dark:bg-gray-900 z-50"> -->
    <aside :class="[
      'w-80 border-l border-slate-200 dark:border-slate-500 bg-white dark:bg-gray-900 flex flex-col transition-all duration-300',
      isShowInfoSection ? 'flex' : 'hidden'
    ]">
      <info-section v-model:isShowInfoSection="isShowInfoSection"/>
    </aside>

  </div>
</template>