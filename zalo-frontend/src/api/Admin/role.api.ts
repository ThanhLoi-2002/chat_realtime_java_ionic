import { IResponse, SetUserRoleType, UserFilter } from "@/types/common";
import axios from "../axios";
import { RoleType } from "@/types/entities";

const getList = async () => {
    return await axios.get<IResponse<any>>(`/admin/system/role`);
};

const getUsersRoles = async (options: UserFilter) => {
    const { page = 0, limit = 20, ...rest } = options

    let filterOptions = `limit=${limit}&page=${page}`;

    for (const [key, value] of Object.entries(rest)) {
        if (value != null || value != undefined)
            filterOptions += `&${key}=${Array.isArray(value) ? value.join(",") : value
                }`;
    }

    return await axios.get<IResponse<any>>(`/admin/system/role/users-roles?${filterOptions}`);
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

const assignRoles = (payload: SetUserRoleType) => {
    return axios.put<IResponse>(`/admin/system/role/assign-roles`, payload);
}

export const roleApi = {
    getList, createOrUpdate, deleteOne, getPermissions, saveSetPermission, getAccess, getUsersRoles, assignRoles
}