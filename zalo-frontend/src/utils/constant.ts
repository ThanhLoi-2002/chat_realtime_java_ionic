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
    // SETTINGS: "/settings",
    // PROFILE: "/profile",
    CHATS: "/chats",
    LANGUAGES: "/languages",
    FRIENDS: "/friends",
    JOIN_GROUP: "/g",
    SCAN: "/scan",
    OA_DASHBOARD: {
        INDEX: "/oa-dashboard",
        HOME: "home",
        CHATBOT: "chatbot",
        MANAGEMENT: "management"
    },

    NOT_FOUND: "/notFound"
}

export const OA_ROUTE = {
    home: () => `${ROUTE.OA_DASHBOARD.INDEX}/${ROUTE.OA_DASHBOARD.HOME}`,
    chatbot: () => `${ROUTE.OA_DASHBOARD.INDEX}/${ROUTE.OA_DASHBOARD.CHATBOT}`,
    management: () => `${ROUTE.OA_DASHBOARD.INDEX}/${ROUTE.OA_DASHBOARD.MANAGEMENT}`,
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

export const STICKER_URL = import.meta.env.VITE_API_STICKER_URL