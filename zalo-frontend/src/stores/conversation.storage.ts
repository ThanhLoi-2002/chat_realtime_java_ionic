import { defineStore } from 'pinia'
import { ConversationType, MediaType, MemberType, MessageType, UserType } from '@/types/entities'
import { conversationApi } from '@/api/conversation.api'
import { toast } from '@/utils/toast'
import { appLimit } from '@/utils/constant'
import { storage } from '@/services/storage.service.'
import { ConversationFilter } from '@/types/common'
import { useConversation } from '@/composables/useConversation'
import { normalizeText } from '@/utils/helper'

interface ConversationState {
    isLoading: boolean,
    conversations: ConversationType[],
    filteredConvs: ConversationType[],
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
        filteredConvs: [],
        conversation: undefined,
        page: -1,
        hasMore: true,
        generalGroup: [],
        userLastMessageId: 0
    }),
    actions: {
        // 1. Lưu danh sách vào máy
        async saveConversationsLocal(list: ConversationType[]) {
            // this.conversations = list;
            await storage.set('conversations', list);
        },

        // 2. Load danh sách khi mở app hoặc vào trang Inbox
        async loadConversationsLocal() {
            const saved = await storage.get<ConversationType[]>('conversations');
            if (saved) {
                this.conversations = saved;
            }
        },

        selectConversation(data?: ConversationType) {
            this.conversation = data
            // console.log(data)
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

        async getConversation(conversationId: number) {
            try {
                const result: any = await conversationApi.getConversation(conversationId);
                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
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
            await this.loadConversationsLocal()

            try {
                this.isLoading = true

                this.page += 1

                const result: any = await conversationApi.getList({ page: this.page, limit: appLimit.conversations });

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

                await this.saveConversationsLocal(this.conversations)

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

        async updateConversation(conv: ConversationType) {
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

            await this.saveConversationsLocal(this.conversations);
        },

        async updateConversationLastMessage(mess: MessageType) {
            const index = this.conversations.findIndex(c => c.id === mess.conversationId);

            if (index !== -1 && this.conversations[index].lastMessage?.id == mess.id) {
                this.conversations[index].lastMessage = mess;
            }

            await this.saveConversationsLocal(this.conversations)
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

        async updateUnreadCount(id: number, unreadCount: number) {
            if (this.conversation) this.conversation.unread = unreadCount

            const index = this.conversations.findIndex(c => c.id === id);

            if (index !== -1) {
                this.conversations[index].unread = unreadCount;
            }

            await this.saveConversationsLocal(this.conversations)
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

            this.saveConversationsLocal(this.conversations)
        },

        updateMemberListRealtime(members: MemberType[]) {
            if (this.conversation) {
                this.conversation.members = members;
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

        async kickMember(memberId: number) {
            try {
                const result: any = await conversationApi.kickMember(this.conversation!.id, memberId);

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        async ordainSilverKey(memberId: number) {
            try {
                const result: any = await conversationApi.ordainSilverKey(this.conversation!.id, memberId);

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        async revokeSilverKey(memberId: number) {
            try {
                const result: any = await conversationApi.revokeSilverKey(this.conversation!.id, memberId);

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        async transferGoldenKey(memberId: number) {
            try {
                const result: any = await conversationApi.transferGoldenKey(this.conversation!.id, memberId);

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },

        kickMemberRealtime(userId: number) {
            if (this.conversation?.members) {
                this.conversation.members = this.conversation.members.filter(u => u.id !== userId);
                this.saveConversationsLocal(this.conversations)
            }
        },

        kickedFromGroupRealtime(conversationId: number) {
            const idx = this.conversations.findIndex(c => c.id == conversationId)

            if (idx !== -1) {
                // Xóa 1 phần tử tại vị trí idx
                this.conversations.splice(idx, 1);
            }

            if (this.conversation?.id == conversationId) this.selectConversation()

            toast({
                color: "primary",
                message: "youHaveBeenRemovedFromGroup"
            })

            this.saveConversationsLocal(this.conversations)
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
                    this.selectConversation()

                    toast({
                        color: "primary",
                        message: "leftTheGroup"
                    })
                }
            }

            this.saveConversationsLocal(this.conversations)
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

            this.saveConversationsLocal(this.conversations)
        },

        setFilter(options?: ConversationFilter) {
            if (!options) {
                this.filteredConvs = []
            } else {
                const { convIds, name } = options
                const { conversationName } = useConversation()

                // Chuyển convIds thành Set để kiểm tra (has) với tốc độ O(1) thay vì O(n)
                const idSet = new Set(convIds || []);
                const searchKeyword = normalizeText(name || "");

                this.filteredConvs = this.conversations.filter((conv) => {
                    // 1. Kiểm tra xem ID có nằm trong danh sách cho phép không (nếu convIds có dữ liệu)
                    const matchesId = idSet.has(conv.id);

                    // 2. Lấy tên hội thoại thông qua helper và kiểm tra keyword
                    const cName = normalizeText(conversationName(conv) || "");
                    const matchesName = cName.includes(searchKeyword);

                    return matchesId && matchesName;
                });
            }
        },

        async findByUserIdsAndTypePrivate(userIds: number[]) {
            try {
                const result: any = await conversationApi.findByUserIdsAndTypePrivate(userIds);

                return result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return []
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
