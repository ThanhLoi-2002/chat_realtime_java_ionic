import { ConversationFilter, IResponse } from "@/types/common";
import axios from "./axios";
import { ConversationType, MediaType, UserType } from "@/types/entities";

const createPrivate = async (data: UserType) => {
  return await axios.post<IResponse<ConversationType>>(
    `/conversations/private?otherId=${data.id}`,
  );
};

const createGroup = async (data: any) => {
  return await axios.post<IResponse<ConversationType>>(
    `/conversations/group`,
    data,
  );
};

const getList = async (filter: ConversationFilter) => {
  const { page, limit = 20 } = filter;
  return await axios.get<IResponse<ConversationType>>(
    `/conversations?page=${page}&limit=${limit}`,
  );
};

const getConversationInfo = async (id: number) => {
  return await axios.get<IResponse>(`/conversations/${id}/info`);
};

const getGroups = async () => {
  return await axios.get<IResponse>(`/conversations/get-groups`);
};

const leaveGroup = async (id: number) => {
  return await axios.delete<IResponse>(`/conversations/${id}/leave-group`);
};

const kickMember = async (id: number, memberId: number) => {
  return await axios.delete<IResponse>(
    `/conversations/${id}/kick-member/${memberId}`,
  );
};

const disbandGroup = async (id: number) => {
  return await axios.delete<IResponse>(`/conversations/${id}/disband-group`);
};

const addMembers = async (id: number, memberIds: number[]) => {
  return await axios.post<IResponse>(
    `/conversations/${id}/add-members`,
    memberIds,
  );
};

const fetchGroupByCode = async (inviteCode: string) => {
  return await axios.get<IResponse>(`/conversations/by-code/${inviteCode}`);
};

const updateAvatar = async (conversationId: number, media: MediaType) => {
  return await axios.put<IResponse>(
    `/conversations/${conversationId}/update-avatar`,
    media,
  );
};

const updateName = async (conversationId: number, name: string) => {
  return await axios.put<IResponse>(
    `/conversations/${conversationId}/update-name`,
    name,
    {
      headers: {
        "Content-Type": "text/plain",
      },
    },
  );
};

const getReadLastMessageId = async (conversationId: number) => {
  return await axios.get<IResponse>(`/conversations/${conversationId}`);
};

const ordainSilverKey = async (conversationId: number, memberId: number) => {
  return await axios.put<IResponse>(`/conversations/${conversationId}/ordain-silver-key/${memberId}`);
};


const revokeSilverKey = async (conversationId: number, memberId: number) => {
  return await axios.put<IResponse>(`/conversations/${conversationId}/revoke-silver-key/${memberId}`);
};

const transferGoldenKey = async (conversationId: number, memberId: number) => {
  return await axios.put<IResponse>(`/conversations/${conversationId}/transfer-golden-key/${memberId}`);
};


export const conversationApi = {
  createPrivate,
  getList,
  createGroup,
  getConversationInfo,
  getGroups,
  leaveGroup,
  kickMember,
  addMembers,
  fetchGroupByCode,
  disbandGroup,
  updateAvatar,
  updateName,
  getReadLastMessageId,
  ordainSilverKey,
  revokeSilverKey,
  transferGoldenKey
};
