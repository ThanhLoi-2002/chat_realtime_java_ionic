import { defineStore } from 'pinia'
import { MessageType } from '@/types/entities'
import { SendMessageType } from '@/types/common';
import { messageApi } from '@/api/message.api';
import { toast } from '@/utils/toast';

interface ConversationState {
    isLoading: boolean,
    messages: MessageType[],
    page: number,
    isOutOfMessage: boolean
}

export const useMessageStore = defineStore('message', {
    state: (): ConversationState => ({
        isLoading: false,
        messages: [],
        page: 0,
        isOutOfMessage: false
    }),
    actions: {
        async sendMessage(data: SendMessageType) {
            try {
                const result: any = await messageApi.sendMessage(data);
                // this.messages.push(result.result)
                return true
                // this.page = result.result.number;
                // if (result.result.totalPages == this.page) this.isOutOfMessage = true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },
        async getMessages(conversationId: number) {
            try {
                const result: any = await messageApi.getMessages({ conversationId });
                this.messages.unshift(...result.result.content)
                this.sort()
                // this.page = result.result.number;
                // if (result.result.totalPages == this.page) this.isOutOfMessage = true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        addNewMessage(data: MessageType) {
            this.messages.push(data)
        },

        sort() {
            this.messages = this.messages.sort((a, b) => {
                const t1 = new Date(b.ct || 0).getTime()
                const t2 = new Date(a.ct || 0).getTime()
                return t2 - t1
            })
        },
    }
})
