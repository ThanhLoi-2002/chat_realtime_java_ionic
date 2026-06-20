import { IResponse } from "@/types/common";
import axios from "../axios";

const pinConv = async (conversationId: number) => {
    return await axios.post<IResponse>(`/conversations/${conversationId}/pin`);
}

const pinMess = async (messId: number, conversationId: number) => {
    return await axios.post<IResponse>(`/conversations/${conversationId}/messages/${messId}/pin`);
}

const removePinMessFromList = async (pinId: number, conversationId: number) => {
    return await axios.delete<IResponse>(`/conversations/${conversationId}/messages/remove-pin-from-list/${pinId}`);
}

const getMessPinList = async (conversationId: number) => {
    return await axios.get<IResponse>(`/conversations/${conversationId}/active-message-pin`);
}

const getPins = async (conversationId: number) => {
    return await axios.get<IResponse>(`/conversations/${conversationId}/pins`);
}

export const pinApi = {
    pinConv, pinMess, removePinMessFromList, getMessPinList, getPins
}