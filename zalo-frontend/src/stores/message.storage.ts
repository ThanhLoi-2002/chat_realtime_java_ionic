import { defineStore } from 'pinia'
import { MessageType } from '@/types/entities'
import { SendMessageType } from '@/types/common';
import { messageApi } from '@/api/message.api';
import { toast } from '@/utils/toast';

interface ConversationState {
    isLoading: boolean,
    messages: MessageType[],
    page: number,
    hasMore: boolean
}

export const useMessageStore = defineStore('message', {
    state: (): ConversationState => ({
        isLoading: false,
        messages: [],
        page: -1,
        hasMore: true
    }),
    actions: {
        async sendMessage(data: SendMessageType) {
            try {
                await messageApi.sendMessage(data);
                return true
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
                this.isLoading = true

                this.page += 1

                const result: any = await messageApi.getMessages({ conversationId, page: this.page });
                const { content, page } = result.result

                this.messages.unshift(...content)
                this.sort()

                if ((page.totalPages - 1) == this.page) this.hasMore = false
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            } finally {
                this.isLoading = false
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

        resetPagination() {
            this.page = -1
            this.hasMore = true
            this.messages = []
        }
    }
})
