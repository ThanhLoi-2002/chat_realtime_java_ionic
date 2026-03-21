import { IResponse } from "@/types/common";
import axios from "./axios";
import { ConversationType, UserType } from "@/types/entities";


const createPrivate = async (data: UserType) => {
    return await axios.post<IResponse<ConversationType>>(`/conversations/private?otherId=${data.id}`);
}

const getList = async () => {
    return await axios.get<IResponse<ConversationType>>(`/conversations?page=0&limit=20`);
}


export const conversationApi = {
    createPrivate, getList
}