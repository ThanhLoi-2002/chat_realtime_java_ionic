export const RANDOM_AVATAR = "https://i.pravatar.cc/150"
export const LANG = "lang"
export const THEME = "theme"
export const ACCESS_TOKEN = "access_token"
export const REFRESH_TOKEN = "refresh_token"
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
    NOT_FOUND: "/notFound"
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