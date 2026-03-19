import { defineStore } from 'pinia'
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
