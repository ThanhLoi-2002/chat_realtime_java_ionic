import { authApi } from '@/api/auth.api'
import { LoginFormType, RegisterFormType } from '@/schema/auth.schema'
import { ACCESS_TOKEN, REFRESH_TOKEN } from '@/utils/constant'
import { setKey } from '@/utils/local'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'
import { useUserStore } from './user.storage'
import { MessagePinType } from '@/types/entities'
import { pinApi } from '@/api/pin.api'

interface State {
    isLoading: boolean
    pinList: MessagePinType[]// ds 3 pin mới nhất
    pins: MessagePinType[] // ds tất cả pin
}

export const usePinStore = defineStore('pin', {
    state: (): State => ({
        isLoading: false,
        pinList: [],
        pins: []
    }),
    actions: {
        async getMessPinList(convId: number) {
            try {
                const result: any = await pinApi.getMessPinList(convId);

                this.pinList = result.result
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
            }
        },

        async getPins(convId: number) {
            try {
                const result: any = await pinApi.getPins(convId);

                this.pins = result.result
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
            }
        },

        async pinMess(messId: number, convId: number) {
            try {
                const result: any = await pinApi.pinMess(messId, convId);

                this.pinList.unshift(result.result)
                
                if (this.pinList.length > 3)
                    this.pinList.pop()

                this.pins.unshift(result.result)
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
            }
        },

        async removePinMessFromList(pinId: number, convId: number) {
            try {
                const result: any = await pinApi.removePinMessFromList(pinId, convId);

                this.pinList.filter(p => p.id != pinId)
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
            }
        },
    }
})
