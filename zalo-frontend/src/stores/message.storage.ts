import { defineStore } from 'pinia'
import { MessageType, ReactionType } from '@/types/entities'
import { MessageFilter, SendMessageType } from '@/types/common';
import { messageApi } from '@/api/message.api';
import { toast } from '@/utils/toast';
import { MessageEnum, ReactionEnum } from '@/types/enum';

interface State {
    isLoading: boolean,
    messages: MessageType[],
    images: MessageType[],
    imagesHasMore: boolean,
    hasMore: boolean,
    previewImage: MessageType | undefined
}

export const useMessageStore = defineStore('message', {
    state: (): State => ({
        isLoading: false,
        messages: [],
        images: [],
        imagesHasMore: true,
        hasMore: true,
        previewImage: undefined
    }),
    actions: {
        setPreviewImage(previewImage?: MessageType) {
            this.previewImage = previewImage
        },

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
        async getMessages(options: MessageFilter) {
            try {
                this.isLoading = true

                const result: any = await messageApi.getMessages(options);
                const { content, page: { size, totalElements } } = result.result

                this.messages.unshift(...content)
                this.sort()

                if (totalElements <= size) this.hasMore = false

                content.forEach((i: MessageType) => {
                    this.addImageMessage(i)
                })
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            } finally {
                this.isLoading = false
            }
        },

        async getImageMessages(options: MessageFilter) {
            try {
                const result: any = await messageApi.getMessages(options);
                const { content, page: { size, totalElements } } = result.result

                content.forEach((i: MessageType) => {
                    this.addImageMessage(i)
                })

                if (totalElements <= size) this.imagesHasMore = false
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
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

        reloadReactions(messageId: number, reactions: ReactionType[]) {
            const idx = this.messages.findIndex(m => m.id === messageId);
            if (idx !== -1) {
                this.messages[idx].reactions = reactions
            }
        },

        addNewMessage(data: MessageType) {
            this.messages.push(data)
            if (data.contentType == MessageEnum.IMAGE) {
                this.images.unshift(data)
            }
        },
        updateMessage(data: MessageType) {
            let idx = this.messages.findIndex(m => m.id === data.id);
            if (idx !== -1) {
                this.messages[idx] = data; // replace
            }

            if (data.stt == -1) {
                if (data.contentType == MessageEnum.IMAGE) {

                    idx = this.images.findIndex(i => i.id == data.id)
                    if (idx !== -1) {
                        this.images = this.images.filter(i => i.id != data.id)
                    }
                }

            }
        },

        sort() {
            this.messages = this.messages.sort((a, b) => {
                const t1 = new Date(b.ct || 0).getTime()
                const t2 = new Date(a.ct || 0).getTime()
                return t2 - t1
            })
        },

        addImageMessage(i: MessageType) {
            // 1. Kiểm tra điều kiện loại nội dung và trạng thái
            const isValidType = i.contentType === MessageEnum.IMAGE || i.contentType === MessageEnum.VIDEO;
            const isValidStatus = i.stt === 1;

            if (isValidType && isValidStatus) {
                // 2. Kiểm tra xem ID đã tồn tại trong mảng images chưa
                const isExisted = this.images.some(img => img.id === i.id);

                if (!isExisted) {
                    this.images.push(i);
                }
            }
        },

        resetPagination() {
            this.hasMore = true
            this.imagesHasMore = true
            this.images = []
            this.messages = []
        }
    }
})
