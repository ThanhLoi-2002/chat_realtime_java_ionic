import { GetPresignedUrlType, IResponse, PaginationType } from "@/types/common";
import axios from "./axios";
import type { LangType } from "@/types/entities";

const getPresignedUrl = async (
    params: GetPresignedUrlType
) => {
    return await axios.get<IResponse>(`/media/presigned`, { params });
};

const getPresignedUrls = async (
    params: GetPresignedUrlType[]
) => {
    return await axios.post<IResponse>(`/media/presigned-urls`, params);
};


export const mediaApi = {
    getPresignedUrl, getPresignedUrls
}