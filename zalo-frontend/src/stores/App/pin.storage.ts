import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'
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

                // this.pinList.unshift(result.result)

                // if (this.pinList.length > 3)
                //     this.pinList.pop()

                // this.pins.unshift(result.result)
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
            }
        },

        pinMessRealtime(messPin: MessagePinType) {
            this.pinList.unshift(messPin)

            if (this.pinList.length > 3)
                this.pinList.pop()

            this.pins.unshift(messPin)
        },

        async removePinMessFromList(pinId: number, convId: number) {
            try {
                const result: any = await pinApi.removePinMessFromList(pinId, convId);

                this.pinList = this.pinList.filter(p => p.id != pinId)
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
            }
        },

        reset() {
            this.pinList = []
            this.pins = []
        }
    }
})
