import { useUserStore } from "@/stores/App/user.storage"
import { ConversationType, MemberType, MessageType } from "@/types/entities"
import { ConversationEnum, MemberRoleEnum, MessageEnum } from "@/types/enum"
import { useTranslate } from "./useTranslate"
import { useConversationStore } from "@/stores/App/conversation.storage"

export function useConversation() {
    const userStorage = useUserStore()
    const { t } = useTranslate()
    const convStorage = useConversationStore()

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

        if (lastMessage.contentType == MessageEnum.SYSTEM) return '';

        if (userStorage.user?.id == lastMessage.sender?.id)
            return t('you')
        else return lastMessage.sender?.username
    }

    const isGoldenKey = () => {
        if (convStorage.conversation) {
            const idx = convStorage.conversation.members.findIndex(m => m.id == userStorage.user?.id)
            if (idx != -1) {
                return convStorage.conversation!.members[idx].role == MemberRoleEnum.GOLDEN_KEY
            }
        }
        return false
    }

    const conversationName = (conversation?: ConversationType) => isGroup(conversation) ? conversation?.name : getRecipient(conversation!)?.username
    const conversationAvatar = (conversation?: ConversationType) => isGroup(conversation) ? conversation?.avatar?.secureUrl : getRecipient(conversation!)?.avatar?.url

    const isAdmin = () => {
        if (convStorage.conversation) {
            const me = convStorage.conversation?.members?.find((m: MemberType) => m.id == userStorage.user?.id)
            return me?.role == MemberRoleEnum.GOLDEN_KEY || me?.role == MemberRoleEnum.SILVER_KEY
        }
        return false
    }

    return {
        isGroup, getRecipient, getUserNameFromLastMessage, conversationName, conversationAvatar, isGoldenKey, isAdmin
    }
}