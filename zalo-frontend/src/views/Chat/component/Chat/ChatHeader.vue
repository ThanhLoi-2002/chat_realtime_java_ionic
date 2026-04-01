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
                <circle-avatar v-else :user="getRecipient(conversationStorage.conversation)" size="size-10"
                    />

                <div class="flex flex-col">
                    <span class="font-medium dark:text-slate-200">
                        {{ conversationName(conversationStorage.conversation!) }}
                    </span>

                    <span class="text-xs bg-green-400 rounded-full w-2.5 h-2.5"
                        v-if="!isGroup(conversationStorage.conversation!)">
                    </span>
                    <div v-else class="flex gap-2 items-center">
                        <i class="fa fa-user text-xs" :class="[style.text.secondary]"/>
                        <span class="text-sm" :class="[style.text.secondary]">
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

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const conversationStorage = useConversationStore()
const systemStorage = useSystemStore()
const { isGroup, getRecipient, conversationName } = useConversation()
const { t } = useTranslate()

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
</script>