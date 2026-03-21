<template>
    <div class="flex gap-2 items-start max-w-[50%]" v-if="!isOwner">
        <circle-avatar :url="message.sender?.avatar?.url || RANDOM_AVATAR" size="size-8" :onClick="() => friendProfileModal?.present()" />
        <div class="flex items-end gap-2">
            <div
                class="bg-white dark:bg-gray-800 dark:text-slate-100 px-4 py-2 rounded-xl text-sm border border-slate-300 flex flex-col">
                <span class="text-xs text-slate-400">{{ message.sender?.username }}</span>
                <span class="py-1">{{ message.content }}</span>
                <span class="text-xs text-slate-400">{{ formatTime(message.ct) }}</span>
            </div>

        </div>
    </div>

    <div class="flex items-end ml-auto gap-2 flex-row-reverse max-w-[50%]" v-else>
        <div
            class="bg-white dark:bg-gray-800 dark:text-slate-100 px-4 py-2 rounded-xl text-sm border border-slate-300 flex flex-col">
            <span>{{ message.content }}</span>
            <span class="text-xs text-slate-400">{{ formatTime(message.ct) }}</span>
        </div>
    </div>
</template>
<script setup lang="ts">
import { RANDOM_AVATAR } from '@/utils/constant';
import { useConversation } from '@/composables/useConversation';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { MessageType } from '@/types/entities';
import { useUserStore } from '@/stores/user.storage';
import { useDateTime } from '@/composables/useDateTime';

const props = defineProps<{
    message: MessageType
    friendProfileModal: any
}>()

const { t } = useTranslate()
const { getRecipient } = useConversation()
const { formatTime } = useDateTime()
const conversationStorage = useConversationStore()
const userStorage = useUserStore()

const isOwner = props.message.sender.id == userStorage.user?.id
console.log(props.message.sender)
</script>