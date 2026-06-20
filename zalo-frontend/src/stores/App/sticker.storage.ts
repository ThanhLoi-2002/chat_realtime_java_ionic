import { stickerApi } from '@/api/App/sticker.api'
import { SendStickerType } from '@/types/common'
import { StickerItemType, StickerType } from '@/types/entities'
import { STORAGE_KEY } from '@/utils/constant'
import { getKey, setKey } from '@/utils/local'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'

interface State {
    stickers: StickerType[]
    recentStickers: StickerItemType[]
    isLoading: boolean
    openPicker: boolean
    pointToStickerId?: string
}

export const useStickerStore = defineStore('sticker', {
    state: (): State => ({
        stickers: [],
        recentStickers: [],
        isLoading: false,
        openPicker: false
    }),
    actions: {
        async getAll() {
            if (this.stickers.length == 0) {
                try {
                    const result: any = await stickerApi.getAll();
                    this.stickers = result.result
                    this.recentStickers = JSON.parse(getKey(STORAGE_KEY) || "[]")
                } catch (e: any) {
                    toast({
                        color: "danger",
                        message: e.message
                    })
                    return undefined
                }
            }
        },

        async sendSticker(payload: SendStickerType) {
            try {
                const result: any = await stickerApi.sendSticker(payload);
                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        addRecentSticker(sticker: StickerItemType) {
            this.recentStickers = [
                sticker,
                ...this.recentStickers.filter(
                    (s) => s.id !== sticker.id,
                ),
            ].slice(0, 24)

            setKey(STORAGE_KEY, this.recentStickers)
        },

        togglePicker(stickerId?: string) {
            this.openPicker = !this.openPicker
            this.pointToStickerId = stickerId
        }
    }
})
