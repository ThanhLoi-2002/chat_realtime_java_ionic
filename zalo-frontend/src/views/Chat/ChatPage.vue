<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import ConversationSection from './ConversationSection.vue';
import { useConversationStore } from '@/stores/conversation.storage';
import { onMounted, onUnmounted, ref } from 'vue';
import ChatContainer from './ChatContainer.vue';
import InfoSection from './InfoSection.vue';
import { useDevice } from '@/composables/useDevice';
import { StompSubscription } from '@stomp/stompjs';
import { socketSubscribe } from '@/utils/websocket';
import { useMessageStore } from '@/stores/message.storage';

const { t } = useTranslate()
const { isMobile } = useDevice()

const conversationStorage = useConversationStore()
const messageStorage = useMessageStore()
const isShowInfoSection = ref(false)

let subNewMessage: StompSubscription | undefined

onMounted(() => {
  subNewMessage = socketSubscribe(`/topic/chat.newMessages`, (msg: any) => {
    console.log("new message:", JSON.parse(msg.body))
    messageStorage.addNewMessage(JSON.parse(msg.body).message)
    conversationStorage.updateConversation(JSON.parse(msg.body).conversation)
  })
})

onUnmounted(() => {
  subNewMessage?.unsubscribe()
})
</script>

<template>

  <div class="flex h-screen bg-white dark:bg-gray-800">
    <!-- CHAT LIST -->
    <section :class="[
      'w-88 max-w-3xl border-r border-slate-200 dark:border-slate-500 flex-col',
      conversationStorage.conversation
        ? 'hidden md:flex'
        : 'flex',
      'flex-1 md:flex-none'
    ]">
      <conversation-section />
    </section>

    <div v-if="!conversationStorage.conversation?.id"
      class="hidden md:flex flex-1 items-center justify-center text-gray-400">
      Select a conversation
    </div>

    <!-- CHAT AREA -->
    <main v-else :class="[
      'flex-1 flex flex-col bg-gray-100 dark:bg-gray-900'
    ]">
      <chat-container v-model:isShowInfoSection="isShowInfoSection" />
    </main>

    <!-- Overlay -->
    <div v-if="isShowInfoSection && isMobile" class="fixed inset-0 bg-black/30 z-40"
      @click="isShowInfoSection = false" />

    <!-- INFO SECTION -->
    <aside :class="[
      'sm:w-80 border-l border-slate-200 dark:border-slate-500 bg-white dark:bg-gray-900 flex flex-col transition-all duration-300',
      isShowInfoSection ? 'flex' : 'hidden',
      isMobile ? 'fixed top-0 right-0 z-50 h-full w-full' : ''
    ]">
      <info-section v-model:isShowInfoSection="isShowInfoSection" />
    </aside>

  </div>
</template>