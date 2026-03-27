import { MessageType } from "./entities";
import { MessageEnum } from "./enum";

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

export type SendMessageType = {
  content?: string
  conversationId?: number
  replyToId?: number
  contentType: MessageEnum
  file?: File
}

export type SendAddFriendRequestType = {
  content?: string
  toId?: number
}

export type BaseFilter = {
  page: number
  limit?: number
  lastId?: number
}

export type MessageFilter = BaseFilter & {
  conversationId: number
}

export type ConversationFilter = BaseFilter