import { oaApi } from '@/api/Oa/oa.api'
import { OaRequestType } from '@/types/common'
import { OaType } from '@/types/entities'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'

interface State {
    isLoading: boolean
    oa?: OaType
    oas: OaType[]
}

export const useOaStore = defineStore('oa', {
    state: (): State => ({
        isLoading: false,
        oa: undefined,
        oas: []
    }),
    actions: {
        async getAllOas() {
            try {
                const result: any = await oaApi.getAllOas();
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return null
            }
        },
        async getAllOasActive() {
            try {
                const result: any = await oaApi.getAllOasActive();
                this.oas = result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return null
            }
        },
        async getById(id: number) {
            try {
                const result: any = await oaApi.getById(id);
                this.oa = result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return null
            }
        },
        async create(data: OaRequestType) {
            try {
                const result: any = await oaApi.create(data);
                console.log(result.result)
                toast({
                    color: "danger",
                    message: result.message
                })
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return null
            }
        },
        async update(id: number, data: OaRequestType) {
            try {
                const result: any = await oaApi.update(id, data);
                this.oa = result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },
    }
})
