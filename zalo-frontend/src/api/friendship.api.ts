import { IResponse, SendAddFriendRequestType } from "@/types/common";
import axios from "./axios";

const getFriend = async (otherId: number) => {
    return await axios.get<IResponse>(`/friends/friend/${otherId}`);
}

const sendAddFriendRequest = async (data: SendAddFriendRequestType) => {
    return await axios.post<IResponse>(`/friends/request`, data);
}

const getReceived = async () => {
    return await axios.get<IResponse>(`/friends/received`);
}

const getSent = async () => {
    return await axios.get<IResponse>(`/friends/sent`);
}

export const friendshipApi = {
    getFriend, sendAddFriendRequest, getReceived, getSent
}
    