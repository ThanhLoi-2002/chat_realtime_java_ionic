import { defineStore } from 'pinia'
import { MessageType } from '@/types/entities'

interface ConversationState {
    isLoading: boolean,
    messages: MessageType[],
}

export const useMessageStore = defineStore('message', {
    state: (): ConversationState => ({
        isLoading: false,
        messages: [
            {
                id: 1,
            },
            {
                id: 2,
            }
        ] as any,
    }),
    actions: {

    }
})
