import { defineStore } from 'pinia'
import { ConversationType, UserType } from '@/types/entities'
import { conversationApi } from '@/api/conversation.api'
import { toast } from '@/utils/toast'

interface ConversationState {
    isLoading: boolean,
    conversations: ConversationType[],
    conversation?: ConversationType,
    recipient?: UserType
}

export const useConversationStore = defineStore('conversation', {
    state: (): ConversationState => ({
        isLoading: false,
        conversations: [],
        conversation: undefined
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
        async getConversations() {
            try {
                const result: any = await conversationApi.getList();
                const newList = result.result.content || []

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
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
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
                console.log('xóa')
            }

            this.conversations.unshift(conv); // thêm lên đầu
        }

    }
})
