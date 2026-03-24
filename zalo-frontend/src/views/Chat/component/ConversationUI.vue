<template>
  <div class="py-2 px-4 cursor-pointer transition-colors duration-200" :class="selectedConversation?.id == conversation.id
    ? 'bg-blue-200 text-white dark:bg-blue-700 '
    : 'bg-white text-gray-900 hover:bg-gray-100 dark:bg-gray-800 dark:text-gray-100 dark:hover:bg-gray-700'">
    <div class="flex gap-3 items-center">
      <!-- Avatar -->
      <img class="size-10 rounded-full object-cover" :src="conversationAvatar(conversation) || RANDOM_AVATAR" />

      <!-- Content -->
      <div class="flex-1 min-w-0">
        <!-- Top -->
        <div class="flex justify-between items-center">
          <span class="font-medium text-gray-900
                 dark:text-gray-100 truncate">
            {{ conversationName(conversation) }}
          </span>
          <span class="text-xs text-gray-400 dark:text-gray-400">
            {{ timeAgo(conversation.et!) }}
          </span>
        </div>

        <!-- Bottom -->
        <div class="flex justify-between items-center">
          <p class="text-sm truncate
                 text-gray-500 dark:text-gray-400">
            {{ lastMessageContent }}
          </p>

          <!-- Unread badge -->
          <span class="ml-2 flex items-center justify-center
                 min-w-5 h-5 px-1
                 text-xs font-medium
                 rounded-full
                 bg-blue-500 text-white">
            3
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useConversation } from '@/composables/useConversation';
import { useDateTime } from '@/composables/useDateTime';
import { ConversationType } from '@/types/entities';
import { RANDOM_AVATAR } from '@/utils/constant';
import { computed } from 'vue';
const props = defineProps<{
  conversation: ConversationType
  selectedConversation?: ConversationType
}>()

const { getUserNameFromLastMessage, conversationAvatar, conversationName } = useConversation()
const { timeAgo } = useDateTime()

const lastMessageContent = computed(() => `${getUserNameFromLastMessage(props.conversation.lastMessage)}: ${props.conversation.lastMessage?.content}`)
</script>