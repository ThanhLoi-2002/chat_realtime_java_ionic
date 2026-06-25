import { roleApi } from '@/api/Admin/role.api'
import { SetUserRoleType, UserFilter } from '@/types/common'
import { RoleType } from '@/types/entities'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'

interface State {
    roles: RoleType[]
    permissions: PermissionTree[],
    isLoading: boolean
    userRole: SetUserRoleType 
}

interface PermissionTree {
    app: string
    modules: {
        module: string
        permissions: string[]
    }[]
}

export const useAdminRoleStore = defineStore('adminRole', {
    state: (): State => ({
        roles: [],
        permissions: [],
        isLoading: false,
        userRole: { id: 0, username: '', phone: '', roleIds: []}
    }),
    actions: {
        async add(data: Omit<RoleType, 'id' | 'module'>) {
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
        async update(data: Omit<RoleType, 'id' | 'module'>, id: number) {
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
        async getList() {
            try {
                const result: any = await roleApi.getList();
                this.roles = result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        async getPermissions() {
            try {
                const result: any = await roleApi.getPermissions();
                console.log(result.result)
                this.permissions = result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },
        async delete(id: number) {
            try {
                const result: any = await roleApi.deleteOne(id);
                this.roles = this.roles.filter(item => item.id !== id)
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },
        async saveSetPermission(payload: Record<string, number[]>) {
            try {
                const result: any = await roleApi.saveSetPermission(payload);

                toast({
                    color: "success",
                    message: result.message
                })
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        async getAccess(payload: string[]) {
            try {
                const result: any = await roleApi.getAccess(payload);

                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return []
            }
        },

        async getUsersRoles(options: UserFilter) {
            try {
                const result: any = await roleApi.getUsersRoles(options);
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return []
            }
        },

        setUserRole(data?: SetUserRoleType) {
            this.userRole = data || { id: 0, username: '', phone: '', roleIds: []}
        },

        async assignRoles(payload: SetUserRoleType) {
            try {
                const result: any = await roleApi.assignRoles(payload);
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