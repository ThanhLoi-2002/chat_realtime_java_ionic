import { IResponse } from "@/types/common";
import axios from "../axios";
import { OaCategoryFormType } from "@/schema/Admin/oaCategory.schema";

const getAll = async () => {
    return await axios.get<IResponse<any>>(`/admin/oa/category`);
}

const create = async (data: OaCategoryFormType) => {
    return await axios.post<IResponse<any>>(`/admin/oa/category`, data);
}

const update = async (id: number, data: OaCategoryFormType) => {
    return await axios.put<IResponse<any>>(`/admin/oa/category/${id}`, data);
}

const remove = async (id: number) => {
    return await axios.delete<IResponse<any>>(`/admin/oa/category/${id}`);
}

export const oaCategoryApi = {
    create, update, getAll, remove
}