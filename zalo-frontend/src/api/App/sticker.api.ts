import { IResponse, PromtStickerType, SendStickerType } from "@/types/common";
import axios from "../axios";
import axiosV2 from "../axiosV2";
import { StickerItemType } from "@/types/entities";

const getAll = async () => {
    return await axios.get<IResponse>(`/stickers`);
}

const sendSticker = async (payload: SendStickerType) => {
    return await axios.post<IResponse>(`/stickers/${payload.conversationId}`, payload.sticker);
}

const generateSticker = async (payload: PromtStickerType) => {
    const response: any = await axiosV2.post<any>(`/stickers/generate-spritesheet`, payload, {
        responseType: 'blob' // Rất quan trọng để nhận dữ liệu ảnh
    });

    // 1. Lấy metadata từ headers
    const blob = response.data; // Đây là file ảnh binary

    // 2. Chuyển đổi blob thành URL để hiển thị
    const imageUrl = URL.createObjectURL(blob);

    const sticker: StickerItemType = {
        id: "",
        url: imageUrl,
        frameCount: response.headers['frame-count'],
        stickerId: ""
    }

    // 3. Trả về object chứa cả ảnh và thông tin để component sử dụng
    return {
        sticker,
        blob,
        fileName: `sticker_${Date.now()}.png`
    };
}

export const stickerApi = {
    getAll, sendSticker, generateSticker
}