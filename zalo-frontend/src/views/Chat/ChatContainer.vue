<template>
    <!-- HEADER -->
    <ChatHeader :isShowInfoSection="isShowInfoSection"
        @update:isShowInfoSection="val => emit('update:isShowInfoSection', val)" />

    <!-- MESSAGES -->
    <div class="flex-1 relative overflow-hidden">
        <AddFriendBar />
        <!-- Phần cuộn tin nhắn -->
        <div ref="scrollContainer" class="h-full overflow-y-auto px-1 pt-2 pb-5 space-y-2 md:p-6" @scroll="scrollMore">
            <div v-if="messageStorage.isLoading" class="text-center text-gray-400 text-sm py-4">
                <LoadingSpinner />
            </div>

            <div v-for="msg in messagesWithMeta" :key="msg.id">
                <div v-if="msg.showTimeSeparator"
                    class="text-center text-[10px] md:text-xs text-slate-500 dark:text-gray-400 my-6 bg-gray-100 dark:bg-slate-700 w-fit mx-auto py-0.5 px-2 rounded-sm">
                    {{ formatSeparatorTime(msg.ct) }}
                </div>

                <SystemMessage v-if="msg.contentType === MessageEnum.SYSTEM" :msg="msg" />

                <MessageContainer v-else :message="msg" :roles="roles"
                    v-observe-visibility="(entry: IntersectionObserverEntry) => handleMessageVisible(msg, entry)" />
            </div>
        </div>

        <!-- NÚT SCROLL TO BOTTOM - CỐ ĐỊNH BÊN TRONG KHUNG CHAT -->
        <button v-if="showScrollDownButton" @click="handleScrollBottom(true)" class="absolute bottom-4 right-4 md:bottom-6 md:right-6 z-30 
             w-8 h-8 md:w-11 md:h-11 flex items-center justify-center 
             bg-white dark:bg-gray-800 shadow-2xl rounded-2xl 
             border border-gray-200 dark:border-gray-600
             hover:bg-gray-50 dark:hover:bg-gray-700 
             active:scale-95 transition-all duration-200">
            <i class="fa-solid fa-arrow-down mext-sm md:text-xl text-gray-600 dark:text-gray-300"></i>
            <span v-if="unreadCount > 0"
                class="absolute -top-2 -right-1 bg-red-500 text-white text-[8px] font-bold px-1.5 py-0.5 rounded-full border-2 border-white dark:border-gray-800">
                {{ unreadCount > 99 ? '99+' : unreadCount }}
            </span>
        </button>
    </div>

    <Typing :scrollContainer="scrollContainer" />
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { useMessageStore } from '@/stores/message.storage';
import ChatHeader from './component/Chat/ChatHeader.vue';
import MessageContainer from './component/Chat/MessageContainer.vue';
import { useDateTime } from '@/composables/useDateTime';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';
import { useScroll } from '@/composables/useScroll';
import Typing from './component/Chat/Typing.vue';
import { useSystemStore } from '@/stores/system.storage';
import AddFriendBar from './component/Chat/AddFriendBar.vue';
import { MemberRoleEnum, MessageEnum } from '@/types/enum';
import { StompSubscription } from '@stomp/stompjs';
import { socketSubscribe } from '@/utils/websocket';
import { MessageFilter } from '@/types/common';
import { useUserStore } from '@/stores/user.storage';
import SystemMessage from './component/Message/SystemMessage.vue';
import { MessageType } from '@/types/entities';
import { useDebounce } from '@/composables/useDebounce';

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const conversationStorage = useConversationStore()
const messageStorage = useMessageStore()
const userStorage = useUserStore()
const sysStorage = useSystemStore()
const { t } = useTranslate()
const { getTime, formatSeparatorTime } = useDateTime()
const { onScroll, scrollToBottom } = useScroll()
const unreadCount = ref(0) // Số tin nhắn mới chưa đọc khi đang cuộn lên trên
const unreadMessageId = ref<number>(conversationStorage.userLastMessageId);

const roles = ref<Record<number, MemberRoleEnum>>()

const scrollContainer = ref<HTMLElement | null>(null)
const showScrollDownButton = ref(false)

let subReaction: StompSubscription | undefined
let sub: StompSubscription | undefined

const emit = defineEmits(['update:isShowInfoSection'])

const TIME_GAP = 5 * 60 * 1000 // 5 phút
const LONG_GAP = 60 * 60 * 1000 // 1 giờ

const messagesWithMeta = computed(() => {
    return messageStorage.messages.map((msg, index) => {
        const prev = messageStorage.messages[index - 1]
        const next = messageStorage.messages[index + 1]

        const currentTime = getTime(msg.ct)
        const prevTime = prev ? getTime(prev.ct) : 0
        const nextTime = next ? getTime(next.ct) : 0

        const isSamePrev = prev && prev.sender?.id === msg.sender?.id
        const isSameNext = next && next.sender?.id === msg.sender?.id

        // ✅ START OF GROUP (quan trọng nhất)
        const isStartGroup =
            !prev ||
            !isSamePrev ||
            (currentTime - prevTime > TIME_GAP)

        // 🔹 END GROUP (QUAN TRỌNG)
        const isEndGroup =
            !next ||
            !isSameNext ||
            (nextTime - currentTime > TIME_GAP)

        const isEndLongGap =
            next && (nextTime - currentTime > LONG_GAP)

        return {
            ...msg,

            // group theo sender (UI bo góc)
            isFirst: isStartGroup,
            isLast: !isSameNext,

            // ✅ avatar ở đầu group
            showAvatar: isStartGroup,

            // ✅ chỉ hiện time ở cuối group theo TIME_GAP
            showTime: isEndGroup || isEndLongGap,

            // separator
            showTimeSeparator:
                !prev ||
                (currentTime - prevTime > TIME_GAP) ||
                (currentTime - prevTime > LONG_GAP)
        }
    })
})

const { debounced: debouncedRead } = useDebounce(() => {
    checkReadMessages()
}, 500)

// Directive để theo dõi một phần tử có nằm trong tầm mắt hay không
const vObserveVisibility = {
    mounted(el: HTMLElement, binding: any) {
        const observer = new IntersectionObserver(
            (entries) => {
                entries.forEach((entry) => {
                    // Nếu phần tử xuất hiện trong tầm mắt (isIntersecting)
                    if (entry.isIntersecting) {
                        binding.value(entry); // Gọi hàm callback truyền vào
                        // Nếu chỉ muốn đánh dấu một lần rồi thôi, có thể unobserve
                        // observer.unobserve(el); 
                    }
                });
            },
            {
                root: null, // Sử dụng viewport của trình duyệt hoặc container
                threshold: 1.0, // 100% tin nhắn phải hiện ra thì mới tính là đã xem
                rootMargin: '0px 0px -20px 0px' // Offset 20px từ dưới lên để tránh mép dưới quá nhạy
            }
        );
        observer.observe(el);
        (el as any)._observer = observer;
    },
    unmounted(el: HTMLElement) {
        if ((el as any)._observer) (el as any)._observer.disconnect();
    },
};

const handleMessageVisible = (msg: MessageType, entry: IntersectionObserverEntry) => {
    // 1. Chỉ xử lý nếu tin nhắn này chưa được đánh dấu là đã đọc (tùy thuộc vào dữ liệu của bạn)
    // 2. Chỉ xử lý nếu tin nhắn đó là từ người khác gửi đến (sender.id !== userStorage.user.id)

    if (entry.isIntersecting && msg.sender?.id !== userStorage.user?.id) {
        // Cập nhật ID mới nhất: 
        // Giả sử tin nhắn sau có ID lớn hơn hoặc thời gian ct lớn hơn
        if (!unreadMessageId.value || msg.id > unreadMessageId.value) {
            unreadMessageId.value = msg.id;

            // Kích hoạt debounce (hàm này sẽ chờ 500ms rồi mới chạy sendReadRequest)
            debouncedRead();
        }
    }
};

const checkReadMessages = async () => {
    // Tìm tin nhắn cuối cùng đang hiển thị trong viewport và gọi readMessage
    // Tuy nhiên, thường chỉ cần cuộn xuống đáy (isAtBottom) 
    // là đủ để gọi API đánh dấu đã đọc toàn bộ hội thoại.
    if (conversationStorage.conversation &&
        conversationStorage.conversation.lastMessageId) {
        let lastMessageId = 0;
        if (showScrollDownButton.value && conversationStorage.userLastMessageId < (unreadMessageId.value ?? 0)) {
            const result = await messageStorage.readMessage(unreadMessageId.value ?? 0, conversationStorage.conversation.id)
            lastMessageId = unreadMessageId.value ?? 0
            unreadCount.value = result
            conversationStorage.updateUnreadCount(conversationStorage.conversation.id, unreadCount.value)
        } else {
            await messageStorage.readMessage(conversationStorage.conversation.lastMessageId, conversationStorage.conversation.id)
            unreadCount.value = 0
            conversationStorage.updateUnreadCount(conversationStorage.conversation.id, 0)
            lastMessageId = conversationStorage.conversation.lastMessageId

            conversationStorage.updateIsMentionFalse(conversationStorage.conversation.id)
        }

        conversationStorage.getReadLastMessageId(lastMessageId);
    }
}

const scrollMore = () => {
    checkScrollAtBottom()

    // Chỉ gọi load more khi gần đầu trang và còn dữ liệu
    if (messageStorage.hasMore && !messageStorage.isLoading) {
        const { scrollTop } = scrollContainer.value!
        if (scrollTop < 300) {   // gần đầu
            const options: MessageFilter = {
                conversationId: conversationStorage.conversation!.id,
                lastId: messageStorage.messages[0]?.id,
            }
            onScroll(scrollContainer.value, () =>
                messageStorage.getMessages(options)
            )
        }
    }
}

const checkScrollAtBottom = () => {
    if (!scrollContainer.value) return
    const { scrollTop, scrollHeight, clientHeight } = scrollContainer.value
    const distanceFromBottom = scrollHeight - scrollTop - clientHeight

    showScrollDownButton.value = distanceFromBottom > 50
}

const handleScrollBottom = (smooth: boolean) => {
    scrollToBottom(scrollContainer.value!, smooth)
    // Đợi scroll hoàn tất rồi mới ẩn nút
    setTimeout(() => {
        showScrollDownButton.value = false
    }, smooth ? 800 : 100)
}

const waitForImages = async () => {
    const container = scrollContainer.value
    if (!container) return

    const images = container.querySelectorAll('img')

    await Promise.all(
        Array.from(images).map(img => {
            if (img.complete) return Promise.resolve()
            return new Promise(resolve => {
                img.onload = resolve
                img.onerror = resolve
            })
        })
    )
}

const reset = async () => {
    sysStorage.setShowBottomMenu(false)
    messageStorage.resetPagination()
    unreadMessageId.value = conversationStorage.userLastMessageId

    roles.value = conversationStorage.conversation?.members?.reduce((acc, member) => {
        // Chỉ lấy những người là GOLDEN_KEY hoặc SILVER_KEY
        if (member.role === MemberRoleEnum.GOLDEN_KEY || member.role === MemberRoleEnum.SILVER_KEY) {
            acc[member.id] = member.role;
        }
        return acc;
    }, {} as Record<number, MemberRoleEnum>) || {};

    if (conversationStorage.conversation) {
        unreadMessageId.value = conversationStorage.conversation.lastMessage?.id ?? 0
        const options: MessageFilter = {
            conversationId: conversationStorage.conversation!.id,
        }
        await messageStorage.getMessages(options)

        await checkReadMessages()
        conversationStorage.getReadLastMessageId()

        await nextTick()
        await waitForImages()

        handleScrollBottom(false)
    }
}

const resetSubscribe = () => {
    subReaction = socketSubscribe(`/user/queue/chat.conversation.${conversationStorage.conversation?.id}.addReaction`, (msg: any) => {
        const data = JSON.parse(msg.body).reaction
        messageStorage.addReactionRealtime(data)
    })

    subReaction = socketSubscribe(`/user/queue/chat.conversation.${conversationStorage.conversation?.id}.reactions`, (msg: any) => {
        const data = JSON.parse(msg.body)
        messageStorage.reloadReactions(data.messageId, data.reactions)
    })

    sub = socketSubscribe(`/user/queue/chat.conversation.${conversationStorage.conversation?.id}.leaveGroup`, (msg: any) => {
        const userId = JSON.parse(msg.body).userId

        conversationStorage.leaveGroupRealtime(userId, userStorage.user?.id == userId)
    })

    sub = socketSubscribe(`/user/queue/chat.conversation.${conversationStorage.conversation?.id}.addMembers`, (msg: any) => {
        conversationStorage.addMembersRealtime(JSON.parse(msg.body).members)
    })
}

onMounted(async () => {
    reset()
    resetSubscribe()
})

watch(() => conversationStorage.conversation?.id, async () => {
    reset()
    subReaction?.unsubscribe()
    sub?.unsubscribe()
    resetSubscribe()
})

watch(() => messageStorage.messages.length, () => {
    if (!showScrollDownButton.value) {
        handleScrollBottom(true)
    } else {
        const unreadMessages = messageStorage.messages.filter(m => m.id > unreadMessageId.value)
        unreadCount.value = unreadMessages.length
        conversationStorage.updateConversation({ ...conversationStorage.conversation!, unread: unreadCount.value })
    }
})

// update unreadMessageId
watch(() => conversationStorage.userLastMessageId, () => {
    unreadMessageId.value = conversationStorage.userLastMessageId
})
</script>