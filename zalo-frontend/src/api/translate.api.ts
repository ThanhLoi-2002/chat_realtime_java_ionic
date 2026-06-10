import { IResponse, TranslateMessageType } from "@/types/common";
import axios from "./axios";

const translateMessage = async (data: TranslateMessageType) => {
    return await axios.post<IResponse>(`/ai/translate`, data);
}

export const translateApi = {
    translateMessage
}