import { roleApi } from '@/api/Admin/role.api'
import { RoleType } from '@/types/entities'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'

interface State {
    roles: RoleType[]
    isLoading: boolean
}

export const useAdminRoleStore = defineStore('adminRole', {
    state: (): State => ({
        roles: [],
        isLoading: false,
    }),
    actions: {
        async add(data: Omit<RoleType, 'id'>) {
            try {
                const result: any = await roleApi.createOrUpdate(data);
                // reassign cả mảng (reference mới) thay vì push in-place,
                // đảm bảo trigger re-render nhất quán giống delete/getList
                this.roles = [...this.roles, result.result]
                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },
        async update(data: Omit<RoleType, 'id'>, id: number) {
            try {
                const result: any = await roleApi.createOrUpdate(data, id);
                const index = this.roles.findIndex(i => i.id === id)
                if (index != -1) {
                    // reassign cả mảng thay vì gán theo index in-place
                    this.roles = this.roles.map((r, i) => i === index ? result.result : r)
                }

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },
    }
})