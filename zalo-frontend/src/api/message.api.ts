import { IResponse, MessageFilter, SendMessageType, ShareMessageType } from "@/types/common";
import axios from "./axios";
import { MessageType } from "@/types/entities";
import { ReactionEnum } from "@/types/enum";


const sendMessage = async (data: SendMessageType) => {
    const { conversationId, ...rest } = data

    return await axios.post<IResponse<MessageType>>(`/conversations/${conversationId}/messages`, rest);
}

const shareMessage = async (data: ShareMessageType) => {
    return await axios.post<IResponse<MessageType>>(`/conversations/id/messages/share`, data);
}

const getMessages = async (options: MessageFilter) => {
    const { conversationId, page = 0, limit = 20, ...rest } = options

    let filterOptions = `limit=${limit}`;

    for (const [key, value] of Object.entries(rest)) {
        if (value != null || value != undefined)
            filterOptions += `&${key}=${Array.isArray(value) ? value.join(",") : value
                }`;
    }

    return await axios.get<IResponse<MessageType>>(`/conversations/${conversationId}/messages?${filterOptions}`);
}

const deleteMessage = async (id: number, conversationId: number) => {
    return await axios.delete<IResponse>(`/conversations/${conversationId}/messages/${id}`);
}

const readMessage = async (id: number, conversationId: number) => {
    return await axios.post<IResponse>(`/conversations/${conversationId}/messages/${id}/read`);
}

const addReaction = async (id: number, conversationId: number, type: keyof typeof ReactionEnum) => {
    return await axios.post<IResponse>(`/conversations/${conversationId}/messages/reaction/add`, { messageId: id, type });
}

const deleteAllReaction = async (id: number, conversationId: number) => {
    return await axios.delete<IResponse>(`/conversations/${conversationId}/messages/reaction/remove-all?messageId=${id}`);
}

const previewLink = async (link: string) => {
    return await axios.get<IResponse>(`/conversations/1/messages/preview-link?link=${link}`);
}

export const messageApi = {
    sendMessage, getMessages, deleteMessage, readMessage, addReaction, deleteAllReaction, previewLink, shareMessage
}