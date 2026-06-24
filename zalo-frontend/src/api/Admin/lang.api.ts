import { IResponse, PaginationType } from "@/types/common";
import axios from "../axios";
import type { LangType } from "@/types/entities";
import { LangFormType } from "@/schema/lang.schema";

const getList = async (filters: any) => {
    const { page, ...query } = filters;

    let filterOptions = `page=${page ?? 0}&limit=10000`;

    for (const [key, value] of Object.entries(query)) {
        if (value != null || value != undefined)
            filterOptions += `&${key}=${Array.isArray(value) ? value.join(",") : value
                }`;
    }

    return await axios.get<IResponse<PaginationType<LangType>>>(`/admin/system/languages?${filterOptions}`);
};

const getListByLang = async (lang: string) => {
    return await axios.get<IResponse<any>>(`/admin/system/languages/getByLang/${lang}`);
};

const getDetail = async (id: number) => {
    return await axios.get<IResponse<LangType>>(`/admin/system/languages/${id}`);
}

const createOrUpdate = async (data: LangFormType, id?: number) => {
    if (id)
        return await axios.put<IResponse<LangType>>(`/admin/system/languages/${id}`, data);
    else return await axios.post<IResponse<LangType>>(`/admin/system/languages`, data);
}

const deleteOne = async (id: number) => {
    return await axios.delete<IResponse>(`/admin/system/languages/${id}`);
}

export const langApi = {
    getList, getDetail, createOrUpdate, deleteOne, getListByLang
}