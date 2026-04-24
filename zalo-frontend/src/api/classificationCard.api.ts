import { IResponse, UpdateClassificationPositionType } from "@/types/common";
import axios from "./axios";
import { ClassificationCardFormType } from "@/schema/classificationCard.schema";

const create = async (data: ClassificationCardFormType) => {
    return await axios.post<IResponse>(`/classification-card`, data);
}

const update = async (id: number, data: ClassificationCardFormType) => {
    return await axios.put<IResponse>(`/classification-card/${id}`, data);
}

const updatePositions = async (data: UpdateClassificationPositionType[]) => {
    return await axios.put<IResponse>(`/classification-card/update-positions`, data);
}

const getAll = async () => {
    return await axios.get<IResponse>(`/classification-card`);
}

const remove = async (id: number) => {
    return await axios.delete<IResponse>(`/classification-card/${id}`);
}

export const classificationCardApi = {
    create, update, getAll, remove, updatePositions
}