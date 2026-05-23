import { joinGroupApi } from '@/api/joinGroup.api';
import { JoinGroupRequestType } from './../types/entities';
import { JoinGroupRequestDto } from "@/types/common";
import { toast } from "@/utils/toast";
import { defineStore } from "pinia";

interface State {
    isLoading: boolean;
    joinGroupRequests: JoinGroupRequestType[]
}

export const useJoinGroupStore = defineStore("joinGroup", {
    state: (): State => ({
        isLoading: false,
        joinGroupRequests: [],
    }),
    actions: {
        async getJoinGroupRequests(convId: number) {
            try {
                const result: any = await joinGroupApi.getJoinGroupRequests(convId);
                this.joinGroupRequests = result.result;
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message,
                });
                return undefined;
            }
        },

        async joinGroup(data: JoinGroupRequestDto) {
            try {
                const result: any = await joinGroupApi.requestToJoinGroup(data);
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },
    },
});
