import { IResponse, JoinGroupRequestDto } from "@/types/common";
import axios from "./axios";

const requestToJoinGroup = async (data: JoinGroupRequestDto) => {
    return await axios.post<IResponse>(`/join-group-request`, data);
};

const getJoinGroupRequests = async (convId: number) => {
    return await axios.get<IResponse>(`/join-group-request/${convId}`);
};

export const joinGroupApi = {
    requestToJoinGroup, getJoinGroupRequests
};