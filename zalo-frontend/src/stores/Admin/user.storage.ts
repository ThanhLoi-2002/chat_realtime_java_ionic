import { userApi } from '@/api/Admin/user.api'
import { PageType, UserFilter } from '@/types/common'
import { UserType } from '@/types/entities'
import { emptyPage } from '@/utils/constant'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'

interface State {
    isLoading: boolean
    list: PageType<UserType>
}

export const useAdminUserStore = defineStore('adminUser', {
    state: (): State => ({
        isLoading: false,
        list: emptyPage
    }),
    actions: {
        async getList(options: UserFilter) {
            try {
                const result: any = await userApi.getList(options);
                this.list = result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },
    }
})
