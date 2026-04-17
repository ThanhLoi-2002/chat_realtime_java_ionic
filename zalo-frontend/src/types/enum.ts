export enum ConversationEnum {
    PRIVATE = "PRIVATE", GROUP = "GROUP", COMMUNITY = "COMMUNITY"
}

export enum MemberRoleEnum {
    MEMBER = "MEMBER", GOLDEN_KEY = "GOLDEN_KEY", SILVER_KEY = "SILVER_KEY"
}

export enum SystemMetadataEnum {
    UPDATE_GROUP_AVATAR = "UPDATE_GROUP_AVATAR",
    UPDATE_GROUP_NAME = "UPDATE_GROUP_NAME",
    ADD_USERS_TO_GROUP = "ADD_USERS_TO_GROUP",
    CREATE_GROUP = "CREATE_GROUP",
    LEAVE_GROUP = "LEAVE_GROUP",
    REMOVE_MEMBER = "REMOVE_MEMBER",

    ORDAIN_SILVER_KEY = "ORDAIN_SILVER_KEY",
    REVOKE_SILVER_KEY = "REVOKE_SILVER_KEY",
    TRANSFER_GOLDEN_KEY = "TRANSFER_GOLDEN_KEY",

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

export enum ResourceEnum {
    IMAGE = "IMAGE",
    VIDEO = "VIDEO",
    RAW = "RAW"
}

export enum ModuleEnum {
    CONVERSATION = "CONVERSATION",
    MESSAGE = "MESSAGE"
}