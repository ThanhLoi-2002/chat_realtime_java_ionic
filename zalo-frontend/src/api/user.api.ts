import { IResponse } from "@/types/common";
import axios from "./axios";
import type { UserType } from "@/types/entities";

const getMe = async () => {
    return await axios.get<IResponse<UserType>>(`/users/me`);
}

const getDetail = async (id: number) => {
    return await axios.get<IResponse<UserType>>(`/users/${id}`);
}

const getList = async (phone: string) => {
    return await axios.get<IResponse<UserType>>(`/users?page=0&limit=2&phone=${phone}`);
}

const deleteOne = async (id: number) => {
    return await axios.delete<IResponse>(`/users/${id}`);
}

const uploadAvatar = async (file: File) => {
    return await axios.post<IResponse<UserType>>(`/users/upload-avatar`, { file }, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    });
}

const uploadCover = async (file: File) => {
    return await axios.post<IResponse<UserType>>(`/users/upload-cover`, { file }, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    });
}

export const userApi = {
    getDetail, deleteOne, getMe, uploadAvatar, uploadCover, getList
}