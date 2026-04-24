import { MediaType, MessageType } from "./entities";
import { ModuleEnum, MessageEnum, ResourceEnum } from "./enum";

export type IResponse<T = any> = {
  // status: boolean;
  message?: string;
  result?: T;
}

export type PaginationType<T = any> = {
  items: T[]
  total: number
  page: number
  limit: number
  totalPages: number
}

export type FileType = {
  url: string
  publicId: string
}

export type SettingPageType = "setting" | "profile"
export type SearchFriendPageType = "addFriend" | "friendProfile"
export type FriendMenuType = "friendList" | "groupList" | "friendInvitation" | "groupInvitation"

export type LinkMetadataType = {
  title: string
  description: string
  image: string
  url: string
}

export type SendMessageType = {
  content?: string | null
  conversationId?: number
  replyToId?: number
  contentType: MessageEnum
  attachments?: MediaType[]
  linkMetadata?: LinkMetadataType
}

export type SendAddFriendRequestType = {
  content?: string
  toId?: number
}

export type BaseFilter = {
  page?: number
  limit?: number
  lastId?: number
}

export type MessageFilter = BaseFilter & {
  conversationId: number
  contentType?: MessageEnum
  linkMetadata?: boolean
}

export type ConversationFilter = BaseFilter

export type GetPresignedUrlType = {
  folder: string
  resourceType: ResourceEnum
}

export type UploadFileType = {
  folder: string
  file: File
  resourceType: ResourceEnum
}

export type UploadFileRequest = {
  params: UploadFileType[]
  updateProgress?: (index: number, percent: number) => void
  moduleType: ModuleEnum
}

export type SaveFcmTokenType = { deviceId: string, fcmToken: string }

export type UpdateClassificationPositionType = { id: number, position: number }