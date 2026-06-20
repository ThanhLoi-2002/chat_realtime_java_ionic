import { IResponse, StructureSortType } from "@/types/common";
import axios from "../axios";
import { StructureType } from "@/types/entities";
import { AppTypeEnum } from "@/types/enum";

const getTree = async () => {
    return await axios.get<IResponse>(`/structure/tree`);
};

const getMenuByUser = async (appType: AppTypeEnum) => {
    return await axios.get<IResponse>(`/structure/menu-by-user?appType=${appType}`);
};

const createOrUpdate = async (data: Omit<StructureType, 'id'> & { id?: number; }) => {
    return await axios.post<IResponse>(`/structure`, data);
};

const getTrash = async () => {
    return await axios.get<IResponse>(`/structure/trash`);
};

const sort = async (payload: StructureSortType[]) => {
    return await axios.put<IResponse>(`/structure/sort`, payload);
};

const deleteOne = async (id: number) => {
    return await axios.delete<IResponse>(`/structure/${id}`);
};

const restore = async (id: number) => {
    return await axios.put<IResponse>(`/structure${id}`);
};

export const structureApi = {
    getTree, createOrUpdate, getTrash, sort, deleteOne, restore, getMenuByUser
}