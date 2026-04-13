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
            <i class="fa fa-users text-sm" :class="[style.text.secondary]" v-if="isGroup(conversation)" />
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

          <div class="flex gap-2 ml-2">
            <span v-if="conversation.isMention" class="flex items-center justify-center min-w-7 h-5 px-1
                 text-xs font-medium rounded-full bg-blue-500 text-white">
              @
            </span>
            <!-- Unread badge -->
            <span v-if="conversation.unread > 0" class="flex items-center justify-center
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
  </div>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue';
import { useConversation } from '@/composables/useConversation';
import { useDateTime } from '@/composables/useDateTime';
import { useMessage } from '@/composables/useMessage';
import { useTranslate } from '@/composables/useTranslate';
import { useUserStore } from '@/stores/user.storage';
import { ConversationType, MessageType } from '@/types/entities';
import { MessageEnum, SystemMetadataEnum } from '@/types/enum';
import { RANDOM_AVATAR } from '@/utils/constant';
import { computed } from 'vue';
const props = defineProps<{
  conversation: ConversationType
  selectedConversation?: ConversationType
}>()

const { t } = useTranslate()
const userStorage = useUserStore()
const { getUserNameFromLastMessage, conversationAvatar, conversationName, isGroup } = useConversation()
const { timeAgo } = useDateTime()
const { stripMentionTag } = useMessage()

const MAX_UNREAD = 5;

const lastMessageContent = computed(() => {
  const lastMessage: MessageType | undefined = props.conversation.lastMessage
  if (!lastMessage) return ''

  const senderName = lastMessage.sender?.id == userStorage.user?.id ? t("you") : lastMessage.sender?.username

  // 1. Xử lý tin nhắn bị thu hồi
  if (lastMessage.stt == -1) {
    return `${senderName}: ${t("messageHasBeenWithdrawn")}`
  }

  // 2. Xử lý tin nhắn hệ thống (Khớp với tin nhắn bạn vừa viết)
  if (lastMessage.contentType == MessageEnum.SYSTEM) {
    const type = lastMessage.systemMetadata?.type

    switch (type) {
      case SystemMetadataEnum.ADD_USERS_TO_GROUP:
        return `${senderName}: ${t("addedMembersToGroup")}`

      case SystemMetadataEnum.UPDATE_GROUP_NAME:
        return `${senderName}: ${t("haveRecentUpdatedGroupNameTo")} "${lastMessage.systemMetadata?.groupName}"`

      case SystemMetadataEnum.UPDATE_GROUP_AVATAR:
        return `${senderName}: ${t("updatedGroupAvatar")}`

      case SystemMetadataEnum.LEAVE_GROUP:
        return `${senderName}: ${t("leftTheGroup")}`

      case SystemMetadataEnum.REMOVE_MEMBER:
        return `${senderName}: ${t("removedMember")}`

      case SystemMetadataEnum.CREATE_GROUP:
        return `${senderName}: ${t("createdTheGroup")}`

      default:
        return `${senderName}: ${t(lastMessage.content || '')}`
    }
  }

  // 3. Xử lý tin nhắn thông thường (Image, File, Text)
  let content = ''
  switch (lastMessage.contentType) {
    case MessageEnum.IMAGE:
      content = t("sentAnImage")
      break
    case MessageEnum.FILE:
      content = t("sentAFile")
      break
    case MessageEnum.TEXT:
      content = stripMentionTag(lastMessage.content)
      break
    default:
      content = stripMentionTag(lastMessage.content || '')
  }

  return `${getUserNameFromLastMessage(lastMessage)}: ${content}`
})
</script>