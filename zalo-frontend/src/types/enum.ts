export enum ConversationEnum {
    PRIVATE = "PRIVATE", GROUP = "GROUP"
}

export enum MemberRoleEnum {
    MEMBER = "MEMBER", ADMIN = "ADMIN", SILVER_KEY = "SILVER_KEY"
}

export enum MessageEnum {
    TEXT = "TEXT", IMAGE = "IMAGE", FILE = "FILE", VIDEO = "VIDEO", SYSTEM = "SYSTEM", STICKER = "STICKER"
}

export enum DeliveryStatusEnum {
    SENT = "SENT", DELIVERED = "DELIVERED", READ = "READ"
}

export enum FriendshipStatusEnum {
    PENDING = "PENDING",    // đã gửi lời mời
    ACCEPTED = "ACCEPTED",   // đã là bạn
    BLOCKED = 'BLOCKED'
}