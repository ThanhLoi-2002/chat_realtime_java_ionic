import { ConversationFilter, IResponse } from "@/types/common";
import axios from "./axios";
import { ConversationType, UserType } from "@/types/entities";


const createPrivate = async (data: UserType) => {
    return await axios.post<IResponse<ConversationType>>(`/conversations/private?otherId=${data.id}`);
}

const createGroup = async (data: any) => {
    return await axios.post<IResponse<ConversationType>>(`/conversations/group`, data);
}

const getList = async (filter: ConversationFilter) => {
    const { page, limit = 20 } = filter
    return await axios.get<IResponse<ConversationType>>(`/conversations?page=${page}&limit=${limit}`);
}

// const getMembers = async (id: number) => {
//     return await axios.get<IResponse<UserType>>(`/conversations/${id}/members`);
// }

const getConversationInfo = async (id: number) => {
    return await axios.get<IResponse>(`/conversations/${id}/info`);
}

export const conversationApi = {
    createPrivate, getList, createGroup, getConversationInfo
}