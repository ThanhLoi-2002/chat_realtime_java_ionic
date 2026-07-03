import { GetPresignedUrlType, IResponse, MinioPresignedUrlType } from "@/types/common";
import axios from "../axios";

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

const getMinioPresignedUrl = async (
    params: MinioPresignedUrlType
) => {
    return await axios.get<IResponse>(`/media/minio-presigned-url`, { params });
};

const getMinioPresignedUrls = async (
    params: MinioPresignedUrlType[]
) => {
    return await axios.post<IResponse>(`/media/minio-presigned-urls`, params);
};


export const mediaApi = {
    getPresignedUrl, getPresignedUrls, getMinioPresignedUrl, getMinioPresignedUrls
}