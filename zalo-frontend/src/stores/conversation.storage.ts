import { defineStore } from 'pinia'
import { ConversationType, MediaType, MemberType, MessageType, UserType } from '@/types/entities'
import { conversationApi } from '@/api/conversation.api'
import { toast } from '@/utils/toast'

interface ConversationState {
    isLoading: boolean,
    conversations: ConversationType[],
    conversation?: ConversationType,
    page: number
    hasMore: boolean
    generalGroup: ConversationType[],
    userLastMessageId: number
}

export const useConversationStore = defineStore('conversation', {
    state: (): ConversationState => ({
        isLoading: false,
        conversations: [],
        conversation: undefined,
        page: -1,
        hasMore: true,
        generalGroup: [],
        userLastMessageId: 0
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
        async getReadLastMessageId(lastMessageId?: number) {
            if (lastMessageId) {
                this.userLastMessageId = lastMessageId
                return
            }

            try {
                if (this.conversation) {
                    const result: any = await conversationApi.getReadLastMessageId(this.conversation.id);
                    this.userLastMessageId = result.result
                }

            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
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

            if (conv.id == this.conversation?.id) {
                this.conversation = conv
            }
        },

        updateConversationLastMessage(mess: MessageType) {
            const index = this.conversations.findIndex(c => c.id === mess.conversationId);

            if (index !== -1 && this.conversations[index].lastMessage?.id == mess.id) {
                this.conversations[index].lastMessage = mess;
            }
        },

        async getConversationInfo() {
            try {
                if (this.conversation) {
                    const result: any = await conversationApi.getConversationInfo(this.conversation.id);
                    this.generalGroup = result.result.generalGroup;
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

        updateUnreadCount(id: number, unreadCount: number) {
            const index = this.conversations.findIndex(c => c.id === id);

            if (index !== -1) {
                this.conversations[index].unread = unreadCount;
            }
        },

        async addMembers(memberIds: number[]) {
            try {
                const result: any = await conversationApi.addMembers(this.conversation!.id, memberIds);

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        addMembersRealtime(members: MemberType[]) {
            if (this.conversation?.members) {
                this.conversation.members = members;

                const idx = this.conversations.findIndex(c => c.id == this.conversation?.id)

                if (idx != -1) {
                    this.conversations[idx].members = members
                }
            }
        },

        async leaveGroup() {
            try {
                const result: any = await conversationApi.leaveGroup(this.conversation!.id);

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        async disbandGroup() {
            try {
                const result: any = await conversationApi.disbandGroup(this.conversation!.id);

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        disbandGroupRealtime(conversationId: number) {

        },

        leaveGroupRealtime(userId: number, isMe: boolean) {
            if (this.conversation?.members) {
                // Phải gán ngược lại vì .filter trả về mảng mới
                this.conversation.members = this.conversation.members.filter(u => u.id !== userId);
            }

            if (isMe) {
                const idx = this.conversations.findIndex(c => c.id == this.conversation?.id)

                if (idx !== -1) {
                    // Xóa 1 phần tử tại vị trí idx
                    this.conversations.splice(idx, 1);
                }
            }
        },

        async updateAvatar(media: MediaType) {
            try {
                if (this.conversation) {
                    const result: any = await conversationApi.updateAvatar(this.conversation.id, media);

                    return true
                }
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        async updateName(name: string) {
            try {
                if (this.conversation) {
                    const result: any = await conversationApi.updateName(this.conversation.id, name);

                    return true
                }
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        async fetchGroupByCode(inviteCode: string) {
            try {
                const result: any = await conversationApi.fetchGroupByCode(inviteCode);

                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },

        updateIsMentionFalse(id: number) {
            if (this.conversation) {
                this.conversation.isMention = false
            }

            const idx = this.conversations.findIndex(c => c.id == id)

            if (idx !== -1) {
                this.conversations[idx].isMention = false;
            }
        },

        reset() {
            this.conversations = []
            this.conversation = undefined
            this.page = -1
            this.hasMore = true
            this.generalGroup = []
        }

    }
})
