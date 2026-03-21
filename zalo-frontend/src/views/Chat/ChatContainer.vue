<template>
    <!-- HEADER -->
    <ChatHeader :isShowInfoSection="isShowInfoSection"
        @update:isShowInfoSection="val => emit('update:isShowInfoSection', val)" />

    <!-- MESSAGES -->
    <div class="flex-1 overflow-y-auto p-6 space-y-4">
        <MessageContainer v-for="message in messageStorage.messages" :key="message.id" :message="message" :friendProfileModal="friendProfileModal" />
    </div>

    <Modal ref="friendProfileModal" :title="t('profile')">
        <friend-profile-u-i :user="getRecipient(conversationStorage.conversation!)" />
    </Modal>

    <!-- INPUT -->
    <footer class="p-4 border-t
          border-gray-200 dark:border-slate-500
          bg-white dark:bg-gray-900">

        <div class="flex gap-3">
            <input :placeholder="t('typeMessage')" class="flex-1 px-4 py-1.5 rounded-lg
                bg-gray-100 dark:bg-gray-800 dark:text-slate-200" v-model="message" @keyup.enter="sendMessage" />

            <base-button icon="fa-solid fa-paper-plane text-blue-500 cursor-pointer" @click="sendMessage" />
        </div>

    </footer>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { RANDOM_AVATAR } from '@/utils/constant';
import { onMounted, ref } from 'vue';
import { useMessageStore } from '@/stores/message.storage';
import { SendMessageType } from '@/types/common';
import ChatHeader from './component/Chat/ChatHeader.vue';
import { useConversation } from '@/composables/useConversation';
import FriendProfileUI from './component/FriendProfileUI.vue';
import { MessageEnum } from '@/types/enum';
import MessageContainer from './component/Chat/MessageContainer.vue';
import Modal from '@/components/Modal/Modal.vue';

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const conversationStorage = useConversationStore()
const messageStorage = useMessageStore()
const { t } = useTranslate()
const { getRecipient } = useConversation()

const friendProfileModal = ref()
const message = ref('')

const emit = defineEmits(['update:isShowInfoSection'])

const sendMessage = async () => {
    if (!message.value.trim()) return

    const payload: SendMessageType = {
        content: message.value.trim(),
        conversationId: conversationStorage.conversation?.id,
        contentType: MessageEnum.TEXT
    }

    const success = await messageStorage.sendMessage(payload);

    if (success) {
        message.value = ''
    }
}

onMounted(() => {
    messageStorage.getMessages(conversationStorage.conversation!.id)
})
</script>