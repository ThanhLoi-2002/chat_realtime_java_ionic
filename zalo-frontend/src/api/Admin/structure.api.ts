import { IResponse, StructureSortType } from "@/types/common";
import axios from "../axios";
import { StructureType } from "@/types/entities";
import { AppTypeEnum } from "@/types/enum";

const getTree = async () => {
    return await axios.get<IResponse>(`/system/structure/tree`);
};

const getMenuByUser = async (appType: AppTypeEnum) => {
    return await axios.get<IResponse>(`/system/structure/menu-by-user?appType=${appType}`);
};

const getModuleByAppType = async (appType: AppTypeEnum) => {
    return await axios.get<IResponse>(`/system/structure/module-by-app-type?appType=${appType}`);
};

const getControllersByModule = async (moduleId: number) => {
    return await axios.get<IResponse>(`/system/structure/controller-by-module?moduleId=${moduleId}`);
};

const createOrUpdate = async (data: Omit<StructureType, 'id'> & { id?: number; }) => {
    return await axios.post<IResponse>(`/system/structure`, data);
};

const getTrash = async () => {
    return await axios.get<IResponse>(`/system/structure/trash`);
};

const sort = async (payload: StructureSortType[]) => {
    return await axios.put<IResponse>(`/system/structure/sort`, payload);
};

const deleteOne = async (id: number) => {
    return await axios.delete<IResponse>(`/system/structure/${id}`);
};

const restore = async (id: number) => {
    return await axios.put<IResponse>(`/system/structure${id}`);
};

export const structureApi = {
    getTree, createOrUpdate, getTrash, sort, deleteOne, restore, getMenuByUser, getModuleByAppType, getControllersByModule
}