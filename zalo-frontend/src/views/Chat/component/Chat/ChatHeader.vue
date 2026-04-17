<template>
    <header class="h-16 flex items-center justify-between px-6
          bg-white dark:bg-gray-900
          border-b border-gray-200 dark:border-slate-500">

        <div class="flex items-center justify-between gap-3 w-full">
            <div class="flex items-cente gap-3">
                <!-- Back button (mobile only) -->
                <MobileBackButton :onClick="onBack" />

                <GroupAvatar v-if="isGroup(conversationStorage.conversation)"
                    :conversation="conversationStorage.conversation!" />
                <circle-avatar v-else :user="getRecipient(conversationStorage.conversation)" size="size-10" />

                <div class="flex flex-col">
                    <span class="font-medium dark:text-slate-200">
                        {{ conversationName(conversationStorage.conversation!) }}
                    </span>

                    <span class="text-xs rounded-full w-2.5 h-2.5" :class="[isOnline ? 'bg-green-400' : 'bg-gray-400']"
                        v-if="!isGroup(conversationStorage.conversation!)">
                    </span>
                    <div v-else class="flex gap-2 items-center">
                        <i class="fa fa-user text-xs" :class="[style.text.secondary]" />
                        <span class="text-xs hover:text-blue-400 cursor-pointer" :class="[style.text.secondary]"
                            @click="() => memberModal?.present()">
                            {{ conversationStorage.conversation?.members?.length }} {{ t("members") }}
                        </span>
                    </div>
                </div>
            </div>

            <div>
                <i class="fas fa-toggle-off text-2xl cursor-pointer dark:text-white" v-if="!isShowInfoSection"
                    @click="emit('update:isShowInfoSection', !isShowInfoSection)"></i>
                <i class="fas fa-toggle-on text-2xl text-blue-500 cursor-pointer" v-if="isShowInfoSection"
                    @click="emit('update:isShowInfoSection', !isShowInfoSection)"></i>
            </div>
        </div>
        <Modal ref="memberModal" title="">
            <Member :isShowBackButton="false" />
        </Modal>
    </header>
</template>
<script setup lang="ts">
import MobileBackButton from '@/components/Button/MobileBackButton.vue';
import { useConversation } from '@/composables/useConversation';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { useSystemStore } from '@/stores/system.storage';
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue';
import { style } from '@/assets/tailwindcss';
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import Modal from '@/components/Modal/Modal.vue';
import Member from '../../Info/components/Member.vue';
import { onMounted, ref, watch } from 'vue';
import { StompSubscription } from '@stomp/stompjs';
import { socketSubscribe, sockJSSendMessage } from '@/utils/websocket';

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const conversationStorage = useConversationStore()
const systemStorage = useSystemStore()
const { isGroup, getRecipient, conversationName } = useConversation()
const { t } = useTranslate()
const memberModal = ref()
const isOnline = ref(false)

let sub: StompSubscription | undefined

const emit = defineEmits(['update:isShowInfoSection'])

const onBack = () => {
    conversationStorage.selectConversation()
    systemStorage.setShowBottomMenu(true)
}

// const checkUserOnline = () => {
//     if (!isGroup(conversationStorage.conversation)) {
//         if (systemStorage.userIdsOnline[getRecipient(conversationStorage.conversation)!.id]) {
//             isOnline.value = true
//         } else {
//             isOnline.value = false
//         }
//     }
// }

const resetSubscribe = () => {
    sub = socketSubscribe(`/topic/user.status.${getRecipient(conversationStorage.conversation)?.id}`, (msg: any) => {
        isOnline.value = JSON.parse(msg.body).online
        console.log(JSON.parse(msg.body))
    })
}

const emitCheckUserOnline = () => {
    sockJSSendMessage({
        recipientId: getRecipient(conversationStorage.conversation)?.id,
    }, "check.userOnline")
}

onMounted(() => {
    resetSubscribe()
    emitCheckUserOnline()
})

watch(() => conversationStorage.conversation?.id, async () => {
    sub?.unsubscribe()
    resetSubscribe()
    emitCheckUserOnline()
})
</script>