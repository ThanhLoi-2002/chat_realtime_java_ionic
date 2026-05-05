import { FileType, LinkMetadataType } from "./common"
import { ConversationEnum, DeliveryStatusEnum, FriendshipStatusEnum, MemberRoleEnum, MessageEnum, ModuleEnum, ReactionEnum, ResourceEnum, SystemMetadataEnum } from "./enum"

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
    inviteCode: string
    description: string
    avatar: MediaType
    lastMessageId?: number
    lastMessage?: MessageType
    recipient?: UserType
    members: MemberType[]
    unread: number
    isMention: boolean
}

export type MessageType = BaseType & {
    conversationId: number
    senderId: number
    sender: UserType
    content: string
    contentType: MessageEnum
    file: FileType
    replyToMessageId: number
    replyToMessage: MessageType
    reactions: ReactionType[]
    systemMetadata: SystemMetadataType
    attachments: MediaType[]
    linkMetadata: LinkMetadataType
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
    user: UserType
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
    count: number
}

export type MemberType = UserType & {
    role: MemberRoleEnum
    addById: number
    addBy: UserType
}

export type SystemMetadataType = {
    type: SystemMetadataEnum
    addedUsersToGroup: UserType[]
    groupName: string
    groupAvatar: MediaType
    user: UserType
}

export type MediaType = BaseType & {
    name: string
    secureUrl: string
    publicId: string
    moduleType: ModuleEnum
    moduleId: number
    resourceType: ResourceEnum
    format: string
    bytes: number
    width: number
    height: number
    messageContent?: string// content của message 
}

export type ClassificationCardType = BaseType & {
    name: string
    color: string
    position: number
    conversationIds: number[]
}