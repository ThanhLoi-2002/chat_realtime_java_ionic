import { defineStore } from 'pinia'
import { ConversationType, MessageType, UserType } from '@/types/entities'
import { conversationApi } from '@/api/conversation.api'
import { toast } from '@/utils/toast'
import { FileType } from '@/types/common'

interface ConversationState {
    isLoading: boolean,
    conversations: ConversationType[],
    conversation?: ConversationType,
    page: number
    hasMore: boolean
    generalGroup: ConversationType[]
    images: MessageType[]
}

export const useConversationStore = defineStore('conversation', {
    state: (): ConversationState => ({
        isLoading: false,
        conversations: [],
        conversation: undefined,
        page: -1,
        hasMore: true,
        generalGroup: [],
        images: [],
    }),
    actions: {
        selectConversation(data?: ConversationType) {
            this.conversation = data
        },
        async createPrivate(user: UserType) {
            try {
                const result: any = await conversationApi.createPrivate(user);
                this.conversation = result.result
                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },
        async createGroup(data: any) {
            try {
                const result: any = await conversationApi.createGroup(data);
                // this.conversation = result.result
                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },
        async getConversations() {
            try {
                this.isLoading = true

                this.page += 1

                const result: any = await conversationApi.getList({ page: this.page });

                const { content, page } = result.result

                const newList = content || []

                // Map để loại trùng theo id
                const map = new Map<number, ConversationType>()

                // 1. Add old trước
                this.conversations.forEach((c: ConversationType) => {
                    map.set(c.id, c)
                })

                // 2. Add new (ghi đè nếu trùng → lấy data mới)
                newList.forEach((c: ConversationType) => {
                    map.set(c.id, c)
                })

                this.conversations = Array.from(map.values())

                // 3. Convert lại array + sort
                this.sortConversation()

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
        sortConversation() {
            this.conversations = this.conversations.sort((a, b) => {
                const t1 = new Date(b.lastMessage?.ct || 0).getTime()
                const t2 = new Date(a.lastMessage?.ct || 0).getTime()
                return t1 - t2
            })
        },

        updateConversation(conv: ConversationType) {
            const index = this.conversations.findIndex(c => c.id === conv.id);

            if (index !== -1) {
                // 🔥 đã tồn tại → update + move lên đầu
                this.conversations.splice(index, 1); // xóa vị trí cũ
            }

            this.conversations.unshift(conv); // thêm lên đầu

            this.sortConversation()
        },

        updateConversationLastMessage(mess: MessageType) {
            const index = this.conversations.findIndex(c => c.id === mess.conversationId);

            if (index !== -1 && this.conversations[index].lastMessage?.id == mess.id) {
                this.conversations[index].lastMessage = mess;
            }
        },

        // async getMembers() {
        //     try {
        //         if (this.conversation) {
        //             const result: any = await conversationApi.getMembers(this.conversation.id);
        //             this.conversation.members = result.result;
        //         }

        //         return true
        //     } catch (e: any) {
        //         toast({
        //             color: "danger",
        //             message: e.message
        //         })
        //         return false
        //     }
        // },

        async getConversationInfo() {
            try {
                if (this.conversation) {
                    const result: any = await conversationApi.getConversationInfo(this.conversation.id);
                    this.generalGroup = result.result.generalGroup;
                    this.images = result.result.messages;
                }

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        async getGroups() {
            try {
                const result: any = await conversationApi.getGroups();

                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return []
            }
        },

        updateUnreadCount(id: number) {
            const index = this.conversations.findIndex(c => c.id === id);

            if (index !== -1) {
                this.conversations[index].unread = 0;
            }
        },

        reset() {
            this.conversations = []
            this.conversation = undefined
            this.page = -1
            this.hasMore = true
            this.images = []
            this.generalGroup = []
        }

    }
})
