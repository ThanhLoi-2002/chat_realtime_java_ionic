import { IResponse, StructureSortType } from "@/types/common";
import axios from "../axios";
import { StructureType } from "@/types/entities";
import { AppTypeEnum } from "@/types/enum";

const getTree = async () => {
    return await axios.get<IResponse>(`/admin/system/structure/tree`);
};

const getMenuByUser = async (appType: AppTypeEnum) => {
    return await axios.get<IResponse>(`/admin/system/structure/menu-by-user?appType=${appType}`);
};

const getModuleByAppType = async (appType: AppTypeEnum) => {
    return await axios.get<IResponse>(`/admin/system/structure/module-by-app-type?appType=${appType}`);
};

const getControllersByModule = async (moduleId: number) => {
    return await axios.get<IResponse>(`/admin/system/structure/controller-by-module?moduleId=${moduleId}`);
};

const createOrUpdate = async (data: Omit<StructureType, 'id'> & { id?: number; }) => {
    return await axios.post<IResponse>(`/admin/system/structure`, data);
};

const getTrash = async () => {
    return await axios.get<IResponse>(`/admin/system/structure/trash`);
};

const sort = async (payload: StructureSortType[]) => {
    return await axios.put<IResponse>(`/admin/system/structure/sort`, payload);
};

const deleteOne = async (id: number) => {
    return await axios.delete<IResponse>(`/admin/system/structure/${id}`);
};

const restore = async (id: number) => {
    return await axios.put<IResponse>(`/admin/system/structure${id}`);
};

export const structureApi = {
    getTree, createOrUpdate, getTrash, sort, deleteOne, restore, getMenuByUser, getModuleByAppType, getControllersByModule
}