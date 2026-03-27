import { friendshipApi } from '@/api/friendship.api';
import { SendAddFriendRequestType } from '@/types/common';
import { toast } from '@/utils/toast';
import { defineStore } from 'pinia'

interface State {
    isLoading: boolean
}

export const useFriendshipStore = defineStore('friendship', {
    state: (): State => ({
        isLoading: false,
    }),
    actions: {
        async isFriend(otherId: number) {
            try {
                const result: any = await friendshipApi.isFriend(otherId);
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },
        async sendAddFriendRequest(data: SendAddFriendRequestType) {
            try {
                this.isLoading = true
                const result: any = await friendshipApi.sendAddFriendRequest(data);
                toast({
                    color: "success",
                    message: result.message
                })
                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            } finally {
                this.isLoading = false
            }
        },

        async getRecieved() {
            try {
                const result: any = await friendshipApi.getReceived();
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return []
            }
        },
        async getSent() {
            try {
                const result: any = await friendshipApi.getSent();
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return []
            }
        }
    }
})
