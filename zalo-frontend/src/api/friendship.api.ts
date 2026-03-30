import { IResponse, SendAddFriendRequestType } from "@/types/common";
import axios from "./axios";

const getFriend = async (otherId: number) => {
  return await axios.get<IResponse>(`/friends/friend/${otherId}`);
};

const getFriends = async () => {
  return await axios.get<IResponse>(`/friends`);
};

const sendAddFriendRequest = async (data: SendAddFriendRequestType) => {
  return await axios.post<IResponse>(`/friends/request`, data);
};

const getReceived = async () => {
  return await axios.get<IResponse>(`/friends/received`);
};

const getSent = async () => {
  return await axios.get<IResponse>(`/friends/sent`);
};

const cancel = async (otherId: number) => {
  return await axios.delete<IResponse>(`/friends/cancel/${otherId}`);
};

const reject = async (otherId: number) => {
  return await axios.post<IResponse>(`/friends/reject/${otherId}`);
};

const accept = async (otherId: number) => {
  return await axios.post<IResponse>(`/friends/accept/${otherId}`);
};

const unfriend = async (otherId: number) => {
  return await axios.delete<IResponse>(`/friends/unfriend/${otherId}`);
};

export const friendshipApi = {
  getFriend,
  sendAddFriendRequest,
  getReceived,
  getSent,
  cancel,
  reject,
  accept,
  unfriend,
  getFriends
};
