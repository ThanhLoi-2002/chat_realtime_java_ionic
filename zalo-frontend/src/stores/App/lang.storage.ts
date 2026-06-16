import { langApi } from '@/api/lang.api'
import { LangFormType } from '@/schema/lang.schema'
import { LangType } from '@/types/entities'
import { LANG } from '@/utils/constant'
import { getKey, setKey } from '@/utils/local'
import { toast } from '@/utils/toast'
import { tryCatch } from '@/utils/trycatch'
import { defineStore } from 'pinia'

interface LangState {
    languages: LangType[]
    isLoading: boolean
    lang: string
    langData: Record<string, string>
}

export const useLangStore = defineStore('lang', {
    state: (): LangState => ({
        languages: [],
        isLoading: false,
        lang: getKey(LANG) || "vi",
        langData: {} as Record<string, string>
    }),
    actions: {
        async add(data: LangFormType, callback: () => void) {
            const lang = await tryCatch(async () => {
                const result: any = await langApi.createOrUpdate(data);
                toast({
                    message: result.message
                })
                callback()
                return result.result
            })

            lang && this.languages.push(lang)
        },
        async update(data: LangFormType, id: number, callback: () => void) {
            const lang = await tryCatch(async () => {
                const result: any = await langApi.createOrUpdate(data, id);
                toast({
                    message: result.message
                })
                callback()
                return result.result
            })

            const index = this.languages.findIndex(i => i.id === lang.id)

            this.languages[index] = lang
        },
        async delete(id: number) {
            const result = await tryCatch(async () => {
                const result: any = await langApi.deleteOne(id);
                toast({
                    message: result.message
                })
                return true
            })

            if (result) {
                this.languages = this.languages.filter(item => item.id !== id)
            }

            return result
        },
        async getList(code?: string) {
            await tryCatch(async () => {
                const result: any = await langApi.getList({ code });
                this.languages = result.result.content
            })
        },
        async getDetail(id: number) {
            return await tryCatch(async () => {
                const result: any = await langApi.getDetail(id);
                return result.result
            })
        },
        async getListByLang(lang?: string) {
            // select lang
            if (lang) {
                setKey(LANG, lang);
                this.lang = lang
            } else {
                const lang = getKey(LANG);
                if (!lang) {
                    setKey(LANG, "vi");
                    this.lang = "vi";
                } else this.lang = lang

            }

            await tryCatch(async () => {
                const result: any = await langApi.getListByLang(this.lang);
                this.langData = result.result
                console.log(this.langData)
            })
        }
    }
})
