import { IResponse, SendMessageType } from "@/types/common";
import axios from "./axios";
import { ConversationType, MessageType } from "@/types/entities";


const sendMessage = async (data: SendMessageType) => {
    const { conversationId, ...rest } = data
    return await axios.post<IResponse<MessageType>>(`/conversations/${conversationId}/messages`, rest);
}

const getMessages = async (options: any) => {
    const { conversationId, ...rest } = options
    return await axios.get<IResponse<MessageType>>(`/conversations/${conversationId}/messages?page=0&limit=20`);
}

export const messageApi = {
    sendMessage, getMessages
}