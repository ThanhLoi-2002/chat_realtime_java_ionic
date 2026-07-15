import { IResponse, UserFilter } from "@/types/common";
import axios from "../axios";

const getList = async (options: UserFilter) => {
    const { page = 0, limit = 20, ...rest } = options

    let filterOptions = `limit=${limit}&page=${page}`;

    for (const [key, value] of Object.entries(rest)) {
        if (value != null || value != undefined)
            filterOptions += `&${key}=${Array.isArray(value) ? value.join(",") : value
                }`;
    }

    return await axios.get<IResponse<any>>(`/admin/system/users?${filterOptions}`);
};

export const userApi = {
    getList
}