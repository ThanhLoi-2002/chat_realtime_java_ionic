import { IResponse } from "@/types/common";
import axios from "../axios";

const createSubscriptionOrderPayment = async (packageId: number) => {
    return await axios.post<IResponse<any>>(`/admin/app/payment/subscription-order?packageId=${packageId}`);
}

const getSubscriptionOrderPaymentInfo = async (code: string) => {
    return await axios.get<IResponse<any>>(`/admin/app/payment/subscription-order-payment-info?code=${code}`);
}

export const paymentApi = {
    createSubscriptionOrderPayment, getSubscriptionOrderPaymentInfo
}