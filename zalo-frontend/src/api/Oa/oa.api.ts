import { IResponse, OaRequestType } from "@/types/common";
import axios from "../axios";
import { OaType } from "@/types/entities";

const getAllOas = async () => {
    return await axios.get<IResponse<any>>(`/oa/official-account`);
}

const getAllOasActive = async () => {
    return await axios.get<IResponse<any>>(`/oa/official-account/my`);
}

const getById = async (id: number) => {
    return await axios.get<IResponse<any>>(`/oa/official-account/${id}`);
}

const create = async (data: OaRequestType) => {
    return await axios.post<IResponse<any>>(`/oa/official-account`, data);
}

const update = async (id: number, data: OaType) => {
    console.log(data)
    return await axios.put<IResponse<any>>(`/oa/official-account/${id}`, data);
}

export const oaApi = {
    create, update, getAllOas, getAllOasActive, getById
}