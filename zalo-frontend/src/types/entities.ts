import { FileType } from "./common"
import { ConversationEnum, DeliveryStatusEnum, MemberRoleEnum, MessageEnum } from "./enum"

export type BaseType = {
    id: number
    stt: number
    cu?: number
    createdBy?: UserType
    ct: Date
    eu?: number
    updatedBy?: UserType
    et: Date
}

export type LangType = BaseType & {
    code: string
    vi: string
    en: string
    tw: string
    cn: string
}

export type UserType = BaseType & {
    username: string
    phone: string
    avatar: FileType
    cover: FileType
}

export type ConversationType = BaseType & {
  type: ConversationEnum
  name?: string
  avatar: FileType
  lastMessageId?: number
  lastMessage?: MessageType
  recipient?: UserType
}

export type MessageType = BaseType & {
  conversationId: number
  senderId: number
  sender: UserType
  content: string
  contentType: MessageEnum
  file: FileType
  replyToMessageId: number
}

export type ConversationMemberType = BaseType & {
    conversationId: number
    userId: number
    role: MemberRoleEnum
    addById: number
    lastReadMessageId: number
}

export type MessageStatusType = BaseType & {
    messageId: number
    userId: number
    status: DeliveryStatusEnum
}