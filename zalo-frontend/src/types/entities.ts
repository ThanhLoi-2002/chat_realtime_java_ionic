import { FileType } from "./common"
import { ConversationEnum, DeliveryStatusEnum, FriendshipStatusEnum, MemberRoleEnum, MessageEnum, ReactionEnum } from "./enum"

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
  members: UserType[]
  unread: number
}

export type MessageType = BaseType & {
  conversationId: number
  senderId: number
  sender: UserType
  content: string
  contentType: MessageEnum
  file: FileType
  replyToMessageId: number
  reactions: ReactionType[]
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

export type FriendshipType = BaseType & {
    user1: UserType
    user2: UserType
    status: FriendshipStatusEnum
    actionUserId: number
}

export type ReactionType = BaseType & {
    messageId: number
    type: ReactionEnum
}