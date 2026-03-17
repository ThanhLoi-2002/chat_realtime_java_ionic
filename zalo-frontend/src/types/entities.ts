import { FileType } from "./common"

export type BaseType = {
    id?: number
    stt: number
    cu?: number
    ct?: Date
    eu?: number
    et?: Date
}

export type LangType = BaseType & {
    code: string
    vi: string
    en: string
    tw: string
    cn: string
}

export type UserType = BaseType & {
    username: string
    phone: string
    avatar: FileType
    cover: FileType
}