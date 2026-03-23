<template>
    <div class="flex gap-2 items-start max-w-[50%]" v-if="!isOwner">
        <circle-avatar v-if="message.showAvatar" :url="message.sender?.avatar?.url" size="size-8"
            :onClick="() => friendProfileModal?.present()" />
        <div v-else class="px-4 py-2"></div>
        <!-- BUBBLE -->
        <div :class="[
            'px-4 py-2 text-sm border flex flex-col rounded-lg',
            'bg-white dark:bg-gray-800 dark:text-slate-100 border-slate-300',
        ]">
            <!-- username chỉ hiện đầu group -->
            <span v-if="message.isFirst" class="text-xs text-slate-400">
                {{ message.sender?.username }}
            </span>

            <span class="py-1">{{ message.content }}</span>

            <span v-if="message.showTime" class="text-xs text-slate-400">
                {{ formatTime(message.ct) }}
            </span>
        </div>
    </div>

    <!-- OWNER -->
    <div v-else class="flex ml-auto gap-2 flex-row-reverse max-w-[60%]">

        <div :class="[
            'px-4 py-2 text-sm border flex flex-col rounded-lg',
            'bg-blue-500 text-white border-blue-500',
        ]">
            <span>{{ message.content }}</span>

            <span v-if="message.showTime" class="text-xs text-blue-100">
                {{ formatTime(message.ct) }}
            </span>
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
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';

const props = defineProps<{
    message: any
    friendProfileModal: any
}>()

const { t } = useTranslate()
const { getRecipient } = useConversation()
const { formatTime } = useDateTime()
const conversationStorage = useConversationStore()
const userStorage = useUserStore()

const isOwner = props.message.sender.id == userStorage.user?.id
</script>