import { userApi } from '@/api/user.api'
import router from '@/router'
import { UserType } from '@/types/entities'
import { ACCESS_TOKEN, ROUTE } from '@/utils/constant'
import { deleteKey } from '@/utils/local'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'

interface LangState {
    isLoading: boolean
    isAuthLoading: boolean
    user?: UserType
}

export const useUserStore = defineStore('user', {
    state: (): LangState => ({
        isLoading: false,
        isAuthLoading: false,
        user: undefined
    }),
    actions: {
        async getMe(isGoToChat?: boolean) {
            try {
                this.isAuthLoading = true

                const result: any = await userApi.getMe();
                this.user = result.result

                this.isAuthLoading = false
                isGoToChat && router.push(ROUTE.CHATS)
            } catch (e) {
                router.push(ROUTE.LOGIN)
                deleteKey(ACCESS_TOKEN)
                this.isAuthLoading = false
            }
        },
        async uploadAvatar(file: File) {
            try {
                const result: any = await userApi.uploadAvatar(file);
                this.user = result.result

                toast({
                    message: result.message
                })
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        async uploadCover(file: File) {
            try {
                const result: any = await userApi.uploadCover(file);
                this.user = result.result

                toast({
                    message: result.message
                })
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },
        async searchByPhone(phone: string): Promise<UserType | undefined>{
            try {
                const result: any = await userApi.searchByPhone(phone);
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },
        logout() {
            this.user = undefined
            deleteKey(ACCESS_TOKEN)
            router.push(ROUTE.LOGIN)
        }
    }
})
