import { subscriptionPackageApi } from '@/api/App/subscriptionPackage.api'
import { SubscriptionPackageFormType } from '@/schema/Admin/subscriptionPackage.schema'
import { SubScriptionPackageFilter } from '@/types/common'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'

interface State {
}

export const useSubscriptionPackageStore = defineStore('subscriptionPackage', {
    state: (): State => ({
    }),
    actions: {
        async getAll(filter: SubScriptionPackageFilter) {
            try {
                const { result }: any = await subscriptionPackageApi.getAll(filter);
                return result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },

        async create(payload: SubscriptionPackageFormType) {
            try {
                const { message, result }: any = await subscriptionPackageApi.create(payload);
                toast({
                    color: "success",
                    message
                })

                return result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },

        async update(id: number, payload: SubscriptionPackageFormType) {
            try {
                const { message, result }: any = await subscriptionPackageApi.update(id, payload);
                toast({
                    color: "success",
                    message
                })

                return result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },

        async delete(id: number) {
            try {
                const { message }: any = await subscriptionPackageApi.remove(id);
                toast({
                    color: "success",
                    message
                })
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },

        async getAvailablePackages(filter: SubScriptionPackageFilter) {
            try {
                const { result }: any = await subscriptionPackageApi.getAvailablePackages(filter);
                return result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },

        async getById(id: number) {
            try {
                const { result }: any = await subscriptionPackageApi.getById(id);
                return result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        }
    }
})
