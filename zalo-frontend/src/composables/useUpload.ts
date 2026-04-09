import { mediaApi } from "@/api/media.api";
import { GetPresignedUrlType, UploadFileRequest } from "@/types/common";
import { MediaType } from "@/types/entities";
import { ResourceEnum } from "@/types/enum";
import axios from "axios";
import pLimit from 'p-limit';

export const useUpload = () => {
    // Limit: Chỉ cho phép upload tối đa 3 file cùng lúc để tránh nghẽn mạng
    const limit = pLimit(3);

    const uploadFile = async (file: File, folder: string, resourceType: ResourceEnum) => {
        // 1. Lấy signature từ Backend
        const { result }: any = await mediaApi.getPresignedUrl({ folder, resourceType });
        console.log(result)
        // 2. Chuẩn bị FormData để gửi lên Cloudinary
        const formData = new FormData();
        formData.append("file", file);
        formData.append("api_key", result.api_key);
        formData.append("timestamp", result.timestamp);
        formData.append("signature", result.signature);
        formData.append("folder", result.folder);
        if (result.eager) formData.append("eager", result.eager);
        if (result.eager_async) formData.append("eager_async", "true");

        // 3. Upload trực tiếp
        const url = `https://api.cloudinary.com/v1_1/${result.cloud_name}/${resourceType.toLowerCase()}/upload`;
        const { data } = await axios.post(url, formData);
        console.log(data)

        // 4. Lấy URL chất lượng nhất
        // Nếu là ảnh (xử lý đồng bộ), dùng eager[0].secure_url
        // Nếu là video (xử lý bất đồng bộ), dùng secure_url gốc, 
        // Cloudinary sẽ tự cập nhật bản nén sau vài giây qua cơ chế cache.
        const finalUrl = data.eager ? data.eager[0].secure_url : data.secure_url;

        return {
            url: finalUrl,
            public_id: data.public_id,
            bytes: data.bytes,
            format: data.format
        };
    }

    const uploadFiles = async (dto: UploadFileRequest) => {
        const { moduleType, params, updateProgress } = dto
        // 1. Lấy tất cả signatures trong 1 lần gọi API duy nhất
        const presignedRequests: GetPresignedUrlType[] = params.map(e => ({
            folder: e.folder,
            resourceType: e.resourceType
        }));

        const { result }: any = await mediaApi.getPresignedUrls(presignedRequests);
        // Lưu ý: backend nên trả về 'result' là một mảng tương ứng với thứ tự gửi lên
        console.log(result)
        // 2. Tạo danh sách các Promise upload (song song)
        const uploadTasks = params.map(async (item, index) => {
            const sig = result.presignedUrls[index]; // Lấy chữ ký tương ứng với file này
            const file = item.file;

            const formData = new FormData();
            formData.append("file", file);
            formData.append("api_key", result.apiKey);
            formData.append("timestamp", sig.timestamp);
            formData.append("signature", sig.signature);
            formData.append("folder", sig.folder);
            if (sig.eager) formData.append("eager", sig.eager);

            const url = `https://api.cloudinary.com/v1_1/${result.cloudName}/${item.resourceType.toLowerCase()}/upload`;

            try {
                const { data } = await axios.post(url, formData, {
                    onUploadProgress: (progressEvent) => {
                        // Tính % tiến độ cho file thứ [index]
                        const percent = Math.round((progressEvent.loaded * 100) / (progressEvent.total || 100));
                        updateProgress?.(index, percent)
                    }
                });

                return {
                    secureUrl: data.eager ? data.eager[0].secure_url : data.secure_url,
                    publicId: data.public_id,
                    moduleType,
                    resourceType: item.resourceType,
                    format: data.format,
                    bytes: data.bytes,
                    width: data.width,
                    height: data.height
                } as MediaType;
            } catch (error) {
                console.error(`Lỗi upload file ${index}:`, error);
                throw error; // Ném lỗi để allSettled bắt được
            }
        });

        // 3. Sử dụng allSettled để không bị "chết chùm" nếu có 1 file lỗi
        const results = await Promise.allSettled(uploadTasks);

        // Lọc ra các file thành công để gửi về Server Java
        return results
            .filter(res => res.status === 'fulfilled')
            .map(res => (res as PromiseFulfilledResult<MediaType>).value);
    };

    return { uploadFile, uploadFiles }
}