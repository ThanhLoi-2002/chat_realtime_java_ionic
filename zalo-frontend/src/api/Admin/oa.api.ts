import { BaseFilter, IResponse } from "@/types/common";
import axios from "../axios";
import { OaStatusEnum } from "@/types/enum";

const getAll = async (filter: BaseFilter) => {
    const cleanedFilter: Record<string, any> = {};

    for (const key in filter) {
        if (Object.prototype.hasOwnProperty.call(filter, key)) {
            const value = (filter as Record<string, any>)[key];
            if (value !== undefined && value !== null && value !== '') {
                cleanedFilter[key] = value;
            }
        }
    }

    return await axios.get<IResponse<any>>(`/admin/oa/list`, {
        params: cleanedFilter
    });
}

const updateStatus = async (id: number, status: OaStatusEnum) => {
    return await axios.put<IResponse<any>>(`/admin/oa/list/status/${id}?status=${status}` );
}

export const oaApi = {
    getAll, updateStatus
}