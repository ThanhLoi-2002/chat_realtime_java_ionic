import { IResponse, MessageFilter, SendMessageType } from "@/types/common";
import axios from "./axios";
import { MessageType } from "@/types/entities";


const sendMessage = async (data: SendMessageType) => {
    const { conversationId, ...rest } = data

    const formData = new FormData()

    Object.entries(rest).forEach(([key, value]) => {
        if (value === null || value === undefined) return
        formData.append(key, value as any)
    })

    return await axios.post<IResponse<MessageType>>(`/conversations/${conversationId}/messages`, formData, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    });
}

const getMessages = async (options: MessageFilter) => {
    const { conversationId, page = 0, lastId, limit = 20 } = options
    return await axios.get<IResponse<MessageType>>(`/conversations/${conversationId}/messages?page=${page}&limit=${limit}&lastId=${lastId}`);
}

export const messageApi = {
    sendMessage, getMessages
}