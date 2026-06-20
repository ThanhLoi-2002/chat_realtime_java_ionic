import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'
import { notificationApi } from '@/api/App/notification.api'
import { SaveFcmTokenType } from '@/types/common'

interface LangState {
    isLoading: boolean
}

export const useNotificationStore = defineStore('notification', {
    state: (): LangState => ({
        isLoading: false,
    }),
    actions: {
        async saveFcmToken(data: SaveFcmTokenType) {
            try {
                const result: any = await notificationApi.saveFcmToken(data);
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
            }
        },
    }
})
