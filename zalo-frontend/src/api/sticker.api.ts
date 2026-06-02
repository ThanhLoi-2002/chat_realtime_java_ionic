import { IResponse } from "@/types/common";
import axios from "./axios";

const getAll = async () => {
    return await axios.get<IResponse>(`/stickers`);
}

export const stickerApi = {
    getAll
}