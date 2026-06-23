import { IResponse } from "@/types/common";
import axios from "../axios";
import { RoleType } from "@/types/entities";

const getList = async () => {
    return await axios.get<IResponse<any>>(`/system/role`);
};

const getPermissions = async () => {
    return await axios.get<IResponse<any>>(`/system/role/permissions`);
};

const createOrUpdate = async (data: Omit<RoleType, 'id' | 'module'>, id?: number) => {
    if (id)
        return await axios.put<IResponse>(`/system/role/${id}`, data);
    else return await axios.post<IResponse>(`/system/role`, data);
}

const deleteOne = async (id: number) => {
    return await axios.delete<IResponse>(`/system/role/${id}`);
}

export const roleApi = {
    getList, createOrUpdate, deleteOne, getPermissions
}