import { IResponse } from "@/types/common";
import axios from "../axios";
import { RoleType } from "@/types/entities";

const getList = async () => {
    return await axios.get<IResponse<any>>(`/admin/system/role`);
};

const getPermissions = async () => {
    return await axios.get<IResponse<any>>(`/admin/system/role/permissions`);
};

const getAccess = async (permissions: string[]) => {
    return await axios.post<IResponse<any>>(`/admin/system/role/access`, permissions);
};

const createOrUpdate = async (data: Omit<RoleType, 'id' | 'module'>, id?: number) => {
    if (id)
        return await axios.put<IResponse>(`/admin/system/role/${id}`, data);
    else return await axios.post<IResponse>(`/admin/system/role`, data);
}

const saveSetPermission = async (payload: Record<string, number[]>) => {
    return await axios.post<IResponse>(`/admin/system/role/set-access`, payload);
}

const deleteOne = async (id: number) => {
    return await axios.delete<IResponse>(`/admin/system/role/${id}`);
}

export const roleApi = {
    getList, createOrUpdate, deleteOne, getPermissions, saveSetPermission, getAccess
}