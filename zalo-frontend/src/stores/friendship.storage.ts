import { friendshipApi } from '@/api/friendship.api';
import { toast } from '@/utils/toast';
import { defineStore } from 'pinia'

interface State {
    isShowBottomMenu: boolean
}

export const useFriendshipStore = defineStore('friendship', {
    state: (): State => ({
        isShowBottomMenu: false,
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
    }
})
