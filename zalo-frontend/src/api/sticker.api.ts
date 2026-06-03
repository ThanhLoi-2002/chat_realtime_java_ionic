import { IResponse, SendStickerType } from "@/types/common";
import axios from "./axios";

const getAll = async () => {
    return await axios.get<IResponse>(`/stickers`);
}

const sendSticker = async (payload: SendStickerType) => {
    return await axios.post<IResponse>(`/stickers/${payload.conversationId}`, payload.sticker);
}

export const stickerApi = {
    getAll, sendSticker
}