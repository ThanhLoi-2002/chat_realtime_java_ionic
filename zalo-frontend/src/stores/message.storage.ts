import { defineStore } from 'pinia'
import { MessageType, ReactionType } from '@/types/entities'
import { SendMessageType } from '@/types/common';
import { messageApi } from '@/api/message.api';
import { toast } from '@/utils/toast';
import { ReactionEnum } from '@/types/enum';

interface State {
    isLoading: boolean,
    messages: MessageType[],
    page: number,
    hasMore: boolean
}

export const useMessageStore = defineStore('message', {
    state: (): State => ({
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
        async readMessage(id: number, conversationId: number) {
            try {
                await messageApi.readMessage(id, conversationId);
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

        async deleteMessage(id: number, conversationId: number) {
            try {
                const result: any = await messageApi.deleteMessage(id, conversationId);
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        async addReaction(id: number, conversationId: number, type: keyof typeof ReactionEnum) {
            try {
                await messageApi.addReaction(id, conversationId, type);
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        addReactionRealtime(reaction: ReactionType) {
            const messageIdx = this.messages.findIndex(m => m.id === reaction.messageId);
            if (messageIdx !== -1) {
                const reactionIdx = this.messages[messageIdx].reactions.findIndex(i => i.type == reaction.type && i.cu == reaction.cu);
                if (reactionIdx !== -1) {
                    this.messages[messageIdx].reactions[reactionIdx].count = reaction.count
                } else {
                    this.messages[messageIdx].reactions.push(reaction)
                }
            }
        },

        async deleteAllReaction(id: number, conversationId: number) {
            try {
                await messageApi.deleteAllReaction(id, conversationId);
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        reloadReactions(reactions: ReactionType[]) {
            const idx = this.messages.findIndex(m => m.id === reactions[0]?.messageId);
            if (idx !== -1) {
                this.messages[idx].reactions = reactions
            }
        },

        addNewMessage(data: MessageType) {
            this.messages.push(data)
        },
        updateMessage(data: MessageType) {
            const idx = this.messages.findIndex(m => m.id === data.id);
            if (idx !== -1) {
                this.messages[idx] = data; // replace
            }
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
