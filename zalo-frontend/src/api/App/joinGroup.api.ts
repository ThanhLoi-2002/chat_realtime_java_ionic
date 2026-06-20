import { IResponse, JoinGroupRequestDto } from "@/types/common";
import axios from "../axios";

const requestToJoinGroup = async (data: JoinGroupRequestDto) => {
    return await axios.post<IResponse>(`/join-group-request`, data);
};

const getJoinGroupRequests = async (convId: number) => {
    return await axios.get<IResponse>(`/join-group-request/${convId}`);
};

const approveRequests = async (ids: number[], convId: number) => {
    return await axios.post<IResponse>(`/join-group-request/${convId}/approve`, ids);
};

const removeRequests = async (ids: number[], convId: number) => {
    return await axios.post<IResponse>(`/join-group-request/${convId}/remove`, ids);
};

export const joinGroupApi = {
    requestToJoinGroup, getJoinGroupRequests, approveRequests, removeRequests
};