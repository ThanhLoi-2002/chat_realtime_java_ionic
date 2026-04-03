<template>
  <div class="py-2 px-4 cursor-pointer transition-colors duration-200" :class="selectedConversation?.id == conversation.id
    ? 'bg-blue-200 dark:bg-blue-700 '
    : 'bg-white text-gray-900 hover:bg-gray-100 dark:bg-gray-800 dark:text-gray-100 dark:hover:bg-gray-700'">
    <div class="flex gap-3 items-center">
      <!-- Avatar -->
      <img class="size-10 rounded-full object-cover" :src="conversationAvatar(conversation) || RANDOM_AVATAR"
        v-if="!isGroup(conversation)" />
      <GroupAvatar v-else :conversation="conversation" :is-disabled="true" />

      <!-- Content -->
      <div class="flex-1 min-w-0">
        <!-- Top -->
        <div class="flex justify-between items-center">
          <div class="flex gap-2 items-center">
            <i class="fa fa-users text-sm" :class="[style.text.secondary]" v-if="isGroup(conversation)"/>
            <span class="font-medium text-gray-900
                 dark:text-gray-100 truncate">
              {{ conversationName(conversation) }}
            </span>
          </div>

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
                 min-w-7 h-5 px-1
                 text-xs font-medium
                 rounded-full
                 bg-blue-500 text-white">
            {{ conversation.unread > MAX_UNREAD ? `+${MAX_UNREAD}` : conversation.unread }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue';
import { useConversation } from '@/composables/useConversation';
import { useDateTime } from '@/composables/useDateTime';
import { useTranslate } from '@/composables/useTranslate';
import { ConversationType, MessageType } from '@/types/entities';
import { MessageEnum } from '@/types/enum';
import { RANDOM_AVATAR } from '@/utils/constant';
import { computed } from 'vue';
const props = defineProps<{
  conversation: ConversationType
  selectedConversation?: ConversationType
}>()

const { t } = useTranslate()
const { getUserNameFromLastMessage, conversationAvatar, conversationName, isGroup } = useConversation()
const { timeAgo } = useDateTime()

const MAX_UNREAD = 5;

const lastMessageContent = computed(() => {
  let content = ''
  const lastMessage: MessageType | undefined = props.conversation.lastMessage
  if (lastMessage?.stt == -1) {
    content = t("messageHasBeenWithdrawn")
  }
  else {
    switch (lastMessage?.contentType) {
      case MessageEnum.IMAGE:
        content = t("Vừa gửi 1 ảnh")
        break

      case MessageEnum.FILE:
        content = t("Vừa gửi 1 file")
        break

      case MessageEnum.TEXT:
        content = lastMessage.content
        break

      default:
        content = lastMessage?.content || ''
    }
  }

  if (lastMessage?.contentType == MessageEnum.SYSTEM) {
    return t(`${content}`)
  } else return `${getUserNameFromLastMessage(lastMessage)}: ${content}`
})
</script>