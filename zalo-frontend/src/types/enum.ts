export enum ConversationEnum {
    PRIVATE = "PRIVATE", GROUP = "GROUP"
}

export enum MemberRoleEnum {
    MEMBER = "MEMBER", ADMIN = "ADMIN", SILVER_KEY = "SILVER_KEY"
}

export enum SystemMetadataEnum {
    ADD_USERS_TO_GROUP = "ADD_USERS_TO_GROUP",
    CREATE_GROUP = "CREATE_GROUP",
    LEAVE_GROUP = "LEAVE_GROUP",
    REMOVE_MEMBER = "REMOVE_MEMBER",

    ADD_FRIEND = "ADD_FRIEND"
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

export enum ReactionEnum {
    LIKE = "LIKE",
    LOVE = "LOVE",
    HAHA = 'HAHA',
    WOW = 'WOW',
    SAD = 'SAD',
    ANGRY = 'ANGRY'
}