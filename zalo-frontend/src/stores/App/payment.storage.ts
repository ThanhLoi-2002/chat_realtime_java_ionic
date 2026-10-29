import { paymentApi } from "@/api/App/payment.api";
import { toast } from "@/utils/toast";
import { defineStore } from "pinia";

interface State {
}

export const usePaymentStore = defineStore('payment', {
    state: (): State => ({
    }),
    actions: {
        async createSubscriptionOrderPayment(packageId: number) {
            try {
                const { result }: any = await paymentApi.createSubscriptionOrderPayment(packageId);
                return result
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
            }
        },

        async getSubscriptionOrderPaymentInfo(code: string) {
            try {
                const { result }: any = await paymentApi.getSubscriptionOrderPaymentInfo(code);
                return result
            } catch (e: any) {
                toast({
                    color: 'danger',
                    message: e.message
                })
            }
        },
    }
})