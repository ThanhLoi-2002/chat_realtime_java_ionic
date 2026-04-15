import { defineStore } from 'pinia'
import { MediaType, MessageType, ReactionType } from '@/types/entities'
import { MessageFilter, SendMessageType } from '@/types/common';
import { messageApi } from '@/api/message.api';
import { toast } from '@/utils/toast';
import { MessageEnum, ReactionEnum } from '@/types/enum';

interface State {
    isLoading: boolean,
    messages: MessageType[],
    images: MediaType[],
    imagesHasMore: boolean,
    hasMore: boolean,
    previewImage: MediaType | undefined,
    files: MediaType[],
    filesHasMore: boolean,
}

export const useMessageStore = defineStore('message', {
    state: (): State => ({
        isLoading: false,
        messages: [],
        images: [],
        imagesHasMore: true,
        hasMore: true,
        previewImage: undefined,
        files: [],
        filesHasMore: false,
    }),
    actions: {
        setPreviewImage(previewImage?: MediaType) {
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
                const result: any = await messageApi.readMessage(id, conversationId);
                return result.result
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

                content.forEach((m: MessageType) => {
                    this.addImage(m)
                    this.addFile(m)
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
                    this.addImage(i)
                })

                if (totalElements <= size) this.imagesHasMore = false
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        async getFileMessages(options: MessageFilter) {
            try {
                const result: any = await messageApi.getMessages(options);
                const { content, page: { size, totalElements } } = result.result

                content.forEach((i: MessageType) => {
                    this.addFile(i)
                })

                if (totalElements <= size) this.filesHasMore = false
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
                this.images.unshift(...data.attachments)
            }

            if (data.contentType == MessageEnum.FILE) {
                this.files.unshift({...data.attachments[0], createdBy: data.sender})
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
                        this.images.splice(idx, 1)
                    }
                }

                if (data.contentType == MessageEnum.FILE) {

                    idx = this.files.findIndex(i => i.moduleId == data.id)

                    if (idx !== -1) {
                        this.files.splice(idx, 1)
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

        addImage(m: MessageType) {
            if ((m.contentType == MessageEnum.IMAGE || m.contentType == MessageEnum.VIDEO) && m.stt == 1) {
                m.attachments.forEach((item: MediaType) => {
                    const isExisted = this.images.some(img => img.id === item.id);

                    if (!isExisted && item.stt == 1) {
                        const media: MediaType = {
                            ...item,
                            messageContent: m.content,
                            createdBy: m.sender
                        }
                        this.images.push(media);
                    }
                })
            }
        },

        addFile(m: MessageType) {
            if (m.contentType == MessageEnum.FILE && m.stt == 1) {
                m.attachments.forEach((item: MediaType) => {
                    const isExisted = this.images.some(img => img.id === item.id);

                    if (!isExisted && item.stt == 1) {
                        const media: MediaType = {
                            ...item,
                            createdBy: m.sender
                        }
                        this.files.push(media);
                    }
                })
            }
        },


        resetPagination() {
            this.hasMore = true
            this.imagesHasMore = true
            this.images = []
            this.filesHasMore = true
            this.files = []
            this.messages = []
            console.log('reset')
        }
    }
})
