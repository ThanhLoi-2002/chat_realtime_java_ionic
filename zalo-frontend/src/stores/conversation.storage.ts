import { authApi } from '@/api/auth.api'
import { LoginFormType } from '@/schema/auth.schema'
import { ACCESS_TOKEN } from '@/utils/constant'
import { setKey } from '@/utils/local'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'
import { useUserStore } from './user.storage'
import { ConversationType } from '@/types/entities'

interface ConversationState {
    isLoading: boolean,
    conversations: ConversationType[],
    conversation?: ConversationType
}

export const useConversationStore = defineStore('conversation', {
    state: (): ConversationState => ({
        isLoading: false,
        conversations: [
            {
                id: 1,
            },
            {
                id: 2,
            }
        ] as any,
        conversation: undefined
    }),
    actions: {
        async selectConversation(data?: ConversationType) {
            this.conversation = data
        },
    }
})
