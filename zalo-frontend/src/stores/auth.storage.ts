import { authApi } from '@/api/auth.api'
import { LoginFormType, RegisterFormType } from '@/schema/auth.schema'
import { ACCESS_TOKEN, REFRESH_TOKEN } from '@/utils/constant'
import { setKey } from '@/utils/local'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'
import { useUserStore } from './user.storage'

interface LangState {
    isLoading: boolean
}

export const useAuthStore = defineStore('auth', {
    state: (): LangState => ({
        isLoading: false,
    }),
    actions: {
        async login(data: LoginFormType) {
            try {
                this.isLoading = true

                const result: any = await authApi.login(data);

                setKey(ACCESS_TOKEN, result.result.token)
                setKey(REFRESH_TOKEN, result.result.refreshToken)

                this.isLoading = false

                const userStore = useUserStore()
                userStore.getMe(true)
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
                this.isLoading = false
            }


        },
        async register(data: RegisterFormType) {
            try {
                this.isLoading = true

                const result: any = await authApi.register(data);
                toast({
                    message: result.message
                })

                setKey(ACCESS_TOKEN, result.result.token)

                this.isLoading = false

                const userStore = useUserStore()
                userStore.getMe(true)
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
                this.isLoading = false
            }
        },
    }
})
