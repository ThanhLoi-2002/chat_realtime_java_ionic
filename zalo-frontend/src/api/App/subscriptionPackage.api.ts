import { IResponse, SubScriptionPackageFilter } from "@/types/common";
import axios from "../axios";
import { SubscriptionPackageFormType } from "@/schema/Admin/subscriptionPackage.schema";

const getAll = async (filter: SubScriptionPackageFilter) => {
    return await axios.get<IResponse>(`/subscription-package`, {
        params: filter,
    });
}

const getById = async (id: number) => {
    return await axios.get<IResponse>(`/subscription-package/${id}`);
}

const create = async (payload: SubscriptionPackageFormType) => {
    return await axios.post<IResponse>(`/subscription-package`, payload);
}

const update = async (id: number, payload: SubscriptionPackageFormType) => {
    return await axios.put<IResponse>(`/subscription-package/${id}`, payload);
}

const remove = async (id: number) => {
    return await axios.delete<IResponse>(`/subscription-package/${id}`);
}

const getAvailablePackages = async (filter: SubScriptionPackageFilter) => {
    return await axios.get<IResponse>(`/subscription-package/available-packages`, {
        params: filter,
    });
}

export const subscriptionPackageApi = {
    getAll, create, update, remove, getAvailablePackages, getById
}