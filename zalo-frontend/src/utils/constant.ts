import { PageType } from "@/types/common"

export const RANDOM_AVATAR = "https://i.pravatar.cc/150"
export const LANG = "lang"
export const THEME = "theme"
export const ACCESS_TOKEN = "access_token"
export const REFRESH_TOKEN = "refresh_token"
export const STORAGE_KEY = 'recent_stickers'
export const SEARCH_USERS_RECENT = "search_users_recent"
export const LANG_LABELS: Record<string, string> = {
    code: "Mã",
    vi: "Tiếng Việt",
    en: "English",
    tw: "Đài loan",
    cn: "Trung quốc",
}

export const ROUTE = {
    LOGIN: "/auth/login",
    REGISTER: "/auth/register",
    APP: {
        INDEX: "/app",
        CHATS: "chats",
        FRIENDS: "friends",
        AI_STICKER: "ai-sticker",
        JOIN_GROUP: "/g",
        SCAN: "/scan",
    },
    OA_DASHBOARD: {
        INDEX: "/oa",
        ACCOUNTS: "accounts",
        DASHBOARD: "dashboard",
        // CHATBOT: "chatbot",
        // MANAGEMENT: "management"
    },
    ADMIN_DASHBOARD: {
        INDEX: "/admin",
        // SYSTEM: 'system',
        // STRUCTURE: "structure",
        // ROLE: "role",
        // USER_ROLE: "user-role",
        // USER_ROLE_EDIT: "edit",
        // USER: "user",
        // LANG: "lang",
        // PERMISSION: 'permission',
        // SET_ACCESS: 'set-access'
    },

    NOT_FOUND: "/notFound",
    FORBIDDEN: "/forbidden"
}

export const APP_ROUTE = {
    index: `${ROUTE.APP.INDEX}/${ROUTE.APP.CHATS}`,
    friend: `${ROUTE.APP.INDEX}/${ROUTE.APP.FRIENDS}`,
    aiSticker: `${ROUTE.APP.INDEX}/${ROUTE.APP.AI_STICKER}`,
}

export const OA_ROUTE = {
    accounts: `${ROUTE.OA_DASHBOARD.INDEX}/${ROUTE.OA_DASHBOARD.ACCOUNTS}`,
    home: `${ROUTE.OA_DASHBOARD.INDEX}/${ROUTE.OA_DASHBOARD.DASHBOARD}`,
    // chatbot: `${ROUTE.OA_DASHBOARD.INDEX}/${ROUTE.OA_DASHBOARD.CHATBOT}`,
    // management: `${ROUTE.OA_DASHBOARD.INDEX}/${ROUTE.OA_DASHBOARD.MANAGEMENT}`,
}

export const ADMIN_ROUTE = {
    home: `${ROUTE.ADMIN_DASHBOARD.INDEX}`,
    // structure: `${ROUTE.ADMIN_DASHBOARD.INDEX}/${ROUTE.ADMIN_DASHBOARD.SYSTEM}/${ROUTE.ADMIN_DASHBOARD.STRUCTURE}`,
    // role: `${ROUTE.ADMIN_DASHBOARD.INDEX}/${ROUTE.ADMIN_DASHBOARD.SYSTEM}/${ROUTE.ADMIN_DASHBOARD.ROLE}`,
    // lang: `${ROUTE.ADMIN_DASHBOARD.INDEX}/${ROUTE.ADMIN_DASHBOARD.SYSTEM}/${ROUTE.ADMIN_DASHBOARD.LANG}`,
    // permission: `${ROUTE.ADMIN_DASHBOARD.INDEX}/${ROUTE.ADMIN_DASHBOARD.SYSTEM}/${ROUTE.ADMIN_DASHBOARD.PERMISSION}`,
}

export const EMOJI_MAP = {
    LIKE: '👍',
    LOVE: '❤️',
    HAHA: '😂',
    WOW: '😮',
    SAD: '😢',
    ANGRY: '😡'
}

export type ReactionKey = keyof typeof EMOJI_MAP;
export const qrCodeUrl = import.meta.env.VITE_QR_CODE_URL

export const appLimit = {
    conversations: 15,
    messages: 20,
    imageVideosFirst: 8,
    imageVideos: 20,
    filesFirst: 4,
    files: 20,
    linksFirst: 4,
    links: 20
}

export const emptyPage: PageType<any> = {
  content: [],
  page: {
    number: 0,
    size: 0,
    totalElements: 0,
    totalPages: 0
  }
};

export const STICKER_URL = import.meta.env.VITE_API_STICKER_URL