import { defineStore } from 'pinia'
import { ConversationType, UserType } from '@/types/entities'

interface ConversationState {
    isLoading: boolean,
    conversations: ConversationType[],
    conversation?: ConversationType,
    friend?: UserType
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
        conversation: undefined,
        friend: undefined
    }),
    actions: {
        async selectConversation(data?: ConversationType) {
            this.conversation = data
        },
    }
})
