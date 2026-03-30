import { FriendshipType, UserType } from './../types/entities';
import { friendshipApi } from "@/api/friendship.api";
import { SendAddFriendRequestType } from "@/types/common";
import { toast } from "@/utils/toast";
import { defineStore } from "pinia";

interface State {
    isLoading: boolean;
    friends: UserType[]
    friendshipAccepted: FriendshipType | undefined
}

export const useFriendshipStore = defineStore("friendship", {
    state: (): State => ({
        isLoading: false,
        friends: [],
        friendshipAccepted: undefined
    }),
    actions: {
        async getFriend(otherId: number) {
            try {
                const result: any = await friendshipApi.getFriend(otherId);
                return result.result;
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message,
                });
                return undefined;
            }
        },
        async sendAddFriendRequest(data: SendAddFriendRequestType) {
            try {
                this.isLoading = true;
                const result: any = await friendshipApi.sendAddFriendRequest(data);
                this.friendshipAccepted = result.result
                toast({
                    color: "success",
                    message: result.message,
                });
                return true;
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message,
                });
                return false;
            } finally {
                this.isLoading = false;
            }
        },

        async getRecieved() {
            try {
                const result: any = await friendshipApi.getReceived();
                return result.result;
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message,
                });
                return [];
            }
        },
        async getSent() {
            try {
                const result: any = await friendshipApi.getSent();
                return result.result;
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message,
                });
                return [];
            }
        },
        async accept(otherId: number) {
            try {
                const result: any = await friendshipApi.accept(otherId);

                toast({
                    color: "success",
                    message: result.message,
                });

                return true;
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message,
                });
                return false;
            }
        },
        async reject(otherId: number) {
            try {
                const result: any = await friendshipApi.reject(otherId);

                toast({
                    color: "success",
                    message: result.message,
                });

                return true;
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message,
                });
                return false;
            }
        },
        async cancel(otherId: number) {
            try {
                const result: any = await friendshipApi.cancel(otherId);

                toast({
                    color: "success",
                    message: result.message,
                });

                return true;
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message,
                });
                return false;
            }
        },
        async unfriend(otherId: number) {
            try {
                const result: any = await friendshipApi.unfriend(otherId);
                this.friends = this.friends.filter(f => f.id !== otherId)

                toast({
                    color: "success",
                    message: result.message,
                });

                return true;
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message,
                });
                return false;
            }
        },

        async getFriends() {
            try {
                const result: any = await friendshipApi.getFriends();
                this.friends = result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message,
                });
            }
        },
    },
});
