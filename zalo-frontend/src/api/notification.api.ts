import { IResponse, SaveFcmTokenType } from "@/types/common";
import axios from "./axios";

const saveFcmToken = async (data: SaveFcmTokenType) => {
    return await axios.post<IResponse>(`/notification/save-fcm-token`, data);
}

export const notificationApi = {
    saveFcmToken
}