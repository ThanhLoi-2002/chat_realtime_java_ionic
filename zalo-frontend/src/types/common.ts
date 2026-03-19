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

export enum ConversationEnum {
  PRIVATE, GROUP
}