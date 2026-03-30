import { useUserStore } from "@/stores/user.storage"
import { ConversationType, MessageType } from "@/types/entities"
import { ConversationEnum, MessageEnum } from "@/types/enum"
import { useTranslate } from "./useTranslate"

export function useConversation() {
    const userStorage = useUserStore()
    const { t } = useTranslate()

    const isGroup = (conversation?: ConversationType) => {
        return conversation?.type == ConversationEnum.GROUP
    }

    const getRecipient = (conversation?: ConversationType) => {
        if (userStorage.user?.id == conversation?.recipient?.id)
            return conversation?.createdBy
        else return conversation?.recipient
    }

    const getUserNameFromLastMessage = (lastMessage?: MessageType) => {
        if (!lastMessage) return

        if(lastMessage.contentType == MessageEnum.SYSTEM) return '';

        if (userStorage.user?.id == lastMessage.sender?.id)
            return t('you')
        else return lastMessage.sender?.username
    }

    const conversationName = (conversation?: ConversationType) => isGroup(conversation) ? conversation?.name : getRecipient(conversation!)?.username
    const conversationAvatar = (conversation?: ConversationType) => isGroup(conversation) ? conversation?.avatar?.url : getRecipient(conversation!)?.avatar?.url

    return {
        isGroup, getRecipient, getUserNameFromLastMessage, conversationName, conversationAvatar
    }
}