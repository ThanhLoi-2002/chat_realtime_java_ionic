import { oaApi } from '@/api/Admin/oa.api';
import { BaseFilter } from '@/types/common';
import { OaStatusEnum } from '@/types/enum';
import { toast } from '@/utils/toast';
import { defineStore } from 'pinia'

interface State {
}

export const useOaStore = defineStore('adminOa', {
    state: (): State => ({
    }),
    actions: {
        async updateStatus(id: number, status: OaStatusEnum) {
            try {
                const result: any = await oaApi.updateStatus(id, status);
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },
        async getAll(filter: BaseFilter) {
            try {
                const result: any = await oaApi.getAll(filter);
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
