import { PageType } from "@/types/common"

export const RANDOM_AVATAR = "https://i.pravatar.cc/150"
export const LANG = "lang"
export const OA_ID = "oa_id"
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
        REGISTER_OA: '/register-oa'
    },
    OA_DASHBOARD: {
        INDEX: "/oa",
        ACCOUNTS: "accounts",
        DASHBOARD: "dashboard",
    },
    ADMIN_DASHBOARD: {
        INDEX: "/admin",
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
}

export const ADMIN_ROUTE = {
    home: `${ROUTE.ADMIN_DASHBOARD.INDEX}`,
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

export const MINIO_URL = import.meta.env.VITE_API_MINIO_URL