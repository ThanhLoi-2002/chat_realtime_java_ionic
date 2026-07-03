import { mediaApi } from "@/api/App/media.api";
import { GetPresignedUrlType, MinioUploadFileType, UploadFileRequest } from "@/types/common";
import { MediaType } from "@/types/entities";
import { ModuleEnum } from "@/types/enum";
import { toast } from "@/utils/toast";
import axios from "axios";
import pLimit from 'p-limit';

export const useUploadMinio = () => {
    const mainFolder = "zalo_java_ionic"
    const imageFolder = `${mainFolder}/images`
    const videoFolder = `${mainFolder}/videos`
    const rawFolder = `${mainFolder}/raws`

    // Limit: Chỉ cho phép upload tối đa 3 file cùng lúc để tránh nghẽn mạng
    const limit = pLimit(3);

    const uploadFile = async (dto: MinioUploadFileType) => {
        const { objectName, file } = dto
        try {
            // 1. Lấy signature từ Backend
            const { result }: any = await mediaApi.getMinioPresignedUrl({ objectName });

            // 3. Upload trực tiếp
           await fetch(result.presignedUrl, {
                method: 'PUT',
                headers: {
                    'Content-Type': file.type, // Quan trọng: Phải trùng với Content-Type lúc tạo URL
                },
                body: file // Truyền trực tiếp đối tượng File (hoặc Blob) vào body
            });

            return true;
        } catch (e: any) {
            console.log(e)
            toast({
                message: e.message,
                color: "danger"
            })
            return false
        }
    }

    const uploadFiles = async (dto: UploadFileRequest) => {
        const { moduleType, params, updateProgress } = dto;

        // 1. Lấy tất cả signatures trong 1 lần gọi API duy nhất
        const presignedRequests: GetPresignedUrlType[] = params.map(e => ({
            folder: e.folder,
            resourceType: e.resourceType
        }));

        const { result }: any = await mediaApi.getPresignedUrls(presignedRequests);

        // 2. Tạo danh sách các Promise upload được kiểm soát bởi p-limit
        const uploadTasks = params.map((item, index) => {
            // Bọc toàn bộ logic async vào trong limit()
            return limit(async () => {

                try {
                    const sig = result.presignedUrls[index];
                    const file = item.file;

                    const formData = new FormData();
                    formData.append("file", file);
                    formData.append("api_key", result.apiKey);
                    formData.append("timestamp", sig.timestamp);
                    formData.append("signature", sig.signature);
                    formData.append("folder", sig.folder);
                    if (sig.eager) formData.append("eager", sig.eager);
                    if (sig.eager_async) formData.append("eager_async", "true");

                    const url = `https://api.cloudinary.com/v1_1/${result.cloudName}/${item.resourceType.toLowerCase()}/upload`;


                    const { data } = await axios.post(url, formData, {
                        onUploadProgress: (progressEvent) => {
                            const percent = Math.round((progressEvent.loaded * 100) / (progressEvent.total || 100));
                            updateProgress?.(index, percent);
                        }
                    });

                    return {
                        secureUrl: data.secure_url,
                        publicId: data.public_id,
                        moduleType,
                        resourceType: item.resourceType,
                        format: data.format,
                        bytes: data.bytes,
                        width: data.width,
                        height: data.height,
                        name: file.name.split('.')[0]
                    } as MediaType;
                } catch (error) {
                    console.error(`Lỗi upload file ${index}:`, error);
                    throw error;
                }
            });
        });

        // 3. Sử dụng allSettled để đợi tất cả các task hoàn thành (thành công hoặc thất bại)
        const results = await Promise.allSettled(uploadTasks);

        // Lọc và trả về kết quả
        return results
            .filter(res => res.status === 'fulfilled')
            .map(res => (res as PromiseFulfilledResult<MediaType>).value);
    };
    return { uploadFile, uploadFiles, imageFolder, videoFolder, rawFolder }
}