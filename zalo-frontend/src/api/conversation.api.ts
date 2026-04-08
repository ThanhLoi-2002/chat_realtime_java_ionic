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

const getConversationInfo = async (id: number) => {
    return await axios.get<IResponse>(`/conversations/${id}/info`);
}

const getGroups = async () => {
    return await axios.get<IResponse>(`/conversations/get-groups`);
}

const leaveGroup = async (id: number) => {
    return await axios.delete<IResponse>(`/conversations/${id}/leave-group`);
}

const disbandGroup = async (id: number) => {
    return await axios.delete<IResponse>(`/conversations/${id}/disband-group`);
}

const addMembers = async (id: number, memberIds: number[]) => {
    return await axios.post<IResponse>(`/conversations/${id}/add-members`, memberIds);
}

const fetchGroupByCode = async (inviteCode: string) => {
    return await axios.get<IResponse>(`/conversations/by-code/${inviteCode}`);
}

export const conversationApi = {
    createPrivate, getList, createGroup, getConversationInfo, getGroups, leaveGroup, addMembers, fetchGroupByCode, disbandGroup
}