import { authApi } from '@/api/auth.api'
import { LoginFormType, RegisterFormType } from '@/schema/auth.schema'
import { ACCESS_TOKEN, REFRESH_TOKEN } from '@/utils/constant'
import { setKey } from '@/utils/local'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'
import { useUserStore } from './user.storage'
import { notificationApi } from '@/api/notification.api'
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
