import { IResponse, JoinGroupRequestDto } from "@/types/common";
import axios from "./axios";

const requestToJoinGroup = async (data: JoinGroupRequestDto) => {
    return await axios.post<IResponse>(`/join-group-request`, data);
};

export const joinGroupApi = {
    requestToJoinGroup,
};