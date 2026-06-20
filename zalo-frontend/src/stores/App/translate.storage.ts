import { translateApi } from '@/api/App/translate.api'
import { storage } from '@/services/storage.service.'
import { TranslateMessageType } from '@/types/common'
import { MessageType } from '@/types/entities'
import { translateMessageKey } from '@/utils/cacheKey'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'

interface State {
    isLoading: boolean
}

export const useTranslateStore = defineStore('translate', {
    state: (): State => ({
        isLoading: false,
    }),
    actions: {
        async translateMessage(mess: MessageType, targetLang: string) {
            const cacheKey = translateMessageKey(mess, targetLang)
            try {
                // 1. KIỂM TRA CACHE TRONG SQLITE / INDEXEDDB
                const cachedText = await storage.get<string>(cacheKey);
                if (cachedText) {
                    console.log(`🚀 Lấy thành công bản dịch từ Cache: ${cacheKey}`);
                    return cachedText;
                }

                const payload: TranslateMessageType = {
                    text: mess.content,
                    sourceLang: mess.lang,
                    targetLang
                }
                const result: any = await translateApi.translateMessage(payload);
                const translatedText = result?.result?.translatedText || "";

                // 3. LƯU BẢN DỊCH VÀO SQLITE / INDEXEDDB ĐỂ DÙNG CHO LẦN SAU
                if (translatedText) {
                    await storage.set(cacheKey, translatedText);
                }

                return translatedText;
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return ""
            }
        },
    }
})
