import { joinGroupApi } from '@/api/joinGroup.api';
import { JoinGroupRequestType } from '@/types/entities';
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

        removeRequest(ids: number[]) {
            this.joinGroupRequests = this.joinGroupRequests.filter(e => !ids.includes(e.id))
        },

        async approveRequests(ids: number[], convId: number) {
            try {
                const result: any = await joinGroupApi.approveRequests(ids, convId);
                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },
        async removeRequests(ids: number[], convId: number) {
            try {
                const result: any = await joinGroupApi.removeRequests(ids, convId);
                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        addNewRequestRealtime(data: JoinGroupRequestType) {
            this.joinGroupRequests.push(data)
        }
    },
});
