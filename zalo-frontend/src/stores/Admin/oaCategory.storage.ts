import { oaCategoryApi } from '@/api/Admin/category.api';
import { OaCategoryFormType } from '@/schema/Admin/oaCategory.schema'
import { toast } from '@/utils/toast';
import { defineStore } from 'pinia'

interface State {
}

export const useOaCategoryStore = defineStore('oaCategory', {
    state: (): State => ({
    }),
    actions: {
        async create(data: OaCategoryFormType) {
            try {
                const result: any = await oaCategoryApi.create(data);
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },
        async update(id: number, data: OaCategoryFormType) {
            try {
                const result: any = await oaCategoryApi.update(id, data);
                return result.result
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
                await oaCategoryApi.remove(id);
                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },
        async getAll() {
            try {
                const result: any = await oaCategoryApi.getAll();
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return []
            }
        },
    }
})
