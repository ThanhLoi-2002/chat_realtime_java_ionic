import { IResponse } from "@/types/common";
import axios from "./axios";

const isFriend = async (otherId: number) => {
    return await axios.get<IResponse>(`/friends/is-friend/${otherId}`);
}

export const friendshipApi = {
    isFriend
}
    