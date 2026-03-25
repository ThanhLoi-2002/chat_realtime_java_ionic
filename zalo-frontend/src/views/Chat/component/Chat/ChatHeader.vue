<template>
    <header class="h-16 flex items-center justify-between px-6
          bg-white dark:bg-gray-900
          border-b border-gray-200 dark:border-slate-500">

        <div class="flex items-center justify-between gap-3 w-full">
            <div class="flex items-cente gap-3">
                <!-- Back button (mobile only) -->
                <MobileBackButton :onClick="onBack" />
                <circle-avatar :url="conversationAvatar(conversationStorage.conversation!) ?? RANDOM_AVATAR" size="size-10"
                    :onClick="() => friendProfileModal?.present()" />

                <div class="flex flex-col">
                    <span class="font-medium dark:text-slate-200">
                        {{ conversationName(conversationStorage.conversation!) }}
                    </span>
                    <span class="text-xs bg-green-400 rounded-full w-2.5 h-2.5" v-if="!isGroup(conversationStorage.conversation!)">
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
            <friend-profile-u-i :user="getRecipient(conversationStorage.conversation!)" />
        </Modal>
    </header>
</template>
<script setup lang="ts">
import MobileBackButton from '@/components/Button/MobileBackButton.vue';
import { useConversation } from '@/composables/useConversation';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { useSystemStore } from '@/stores/system.storage';
import { RANDOM_AVATAR } from '@/utils/constant';
import { computed, onMounted, ref, watch } from 'vue';
import FriendProfileUI from '../FriendProfileUI.vue';
import Modal from '@/components/Modal/Modal.vue';

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const conversationStorage = useConversationStore()
const systemStorage = useSystemStore()
const { isGroup, getRecipient, conversationAvatar, conversationName } = useConversation()
const { t } = useTranslate()

const friendProfileModal = ref()

const emit = defineEmits(['update:isShowInfoSection'])

const onBack = () => {
    conversationStorage.selectConversation()
    systemStorage.setShowBottomMenu(true)
}

// const isRecipientOnline = computed(() => {
//   const conversation = conversationStorage.conversation
//   if (!conversation || isGroup(conversation)) return false

//   const user = getRecipient(conversation)
//   if (!user) return false

//   return systemStorage.onlineUsers.has(user.id)
// })

onMounted(() => {

})

watch(() => conversationStorage.conversation, () => {
    
})
</script>