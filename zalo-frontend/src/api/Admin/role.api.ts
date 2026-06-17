import { IResponse } from "@/types/common";
import axios from "../axios";
import { RoleType } from "@/types/entities";

const getList = async () => {
    return await axios.get<IResponse<any>>(`/role`);
};

const createOrUpdate = async (data: Omit<RoleType, 'id'>, id?: number) => {
    if (id)
        return await axios.put<IResponse>(`/role/${id}`, data);
    else return await axios.post<IResponse>(`/role`, data);
}

const deleteOne = async (id: number) => {
    return await axios.delete<IResponse>(`/role/${id}`);
}

export const roleApi = {
    getList, createOrUpdate, deleteOne
}