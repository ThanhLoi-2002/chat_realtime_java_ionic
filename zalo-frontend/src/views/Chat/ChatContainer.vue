<template>
    <!-- HEADER -->
    <ChatHeader :isShowInfoSection="isShowInfoSection"
        @update:isShowInfoSection="val => emit('update:isShowInfoSection', val)" />

    <!-- MESSAGES -->
    <div class="flex-1 relative overflow-hidden">
        <!-- Phần cuộn tin nhắn -->
        <div ref="scrollContainer" class="h-full overflow-y-auto p-1 space-y-1 md:p-6 md:space-y-2" @scroll="scrollMore">
            <div v-if="messageStorage.isLoading" class="text-center text-gray-400 text-sm py-4">
                <LoadingSpinner />
            </div>

            <div v-for="msg in messagesWithMeta" :key="msg.id">
                <div v-if="msg.showTimeSeparator" class="text-center text-[10px] md:text-xs text-slate-500 dark:text-gray-400 my-6 bg-gray-100 dark:bg-slate-700 w-fit mx-auto py-0.5 px-2 rounded-sm">
                    {{ formatSeparatorTime(msg.ct) }}
                </div>

                <MessageContainer :message="msg" :friendProfileModal="friendProfileModal" />
            </div>
        </div>

        <!-- NÚT SCROLL TO BOTTOM - CỐ ĐỊNH BÊN TRONG KHUNG CHAT -->
        <button v-if="showScrollButton" @click="handleScrollBottom(true)" class="absolute bottom-4 right-4 md:bottom-6 md:right-6 z-30 
             w-8 h-8 md:w-11 md:h-11 flex items-center justify-center 
             bg-white dark:bg-gray-800 shadow-2xl rounded-2xl 
             border border-gray-200 dark:border-gray-600
             hover:bg-gray-50 dark:hover:bg-gray-700 
             active:scale-95 transition-all duration-200">
            <i class="fa-solid fa-arrow-down mext-sm md:text-xl text-gray-600 dark:text-gray-300"></i>
        </button>
    </div>

    <Modal ref="friendProfileModal" :title="t('profile')">
        <friend-profile-u-i :user="getRecipient(conversationStorage.conversation!)" />
    </Modal>

    <Typing :scrollContainer="scrollContainer" />
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { useMessageStore } from '@/stores/message.storage';
import ChatHeader from './component/Chat/ChatHeader.vue';
import { useConversation } from '@/composables/useConversation';
import FriendProfileUI from './component/FriendProfileUI.vue';
import MessageContainer from './component/Chat/MessageContainer.vue';
import Modal from '@/components/Modal/Modal.vue';
import { useDateTime } from '@/composables/useDateTime';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';
import { useScroll } from '@/composables/useScroll';
import Typing from './component/Chat/Typing.vue';
import { useSystemStore } from '@/stores/system.storage';

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const conversationStorage = useConversationStore()
const messageStorage = useMessageStore()
const sysStorage = useSystemStore()
const { t } = useTranslate()
const { getRecipient } = useConversation()
const { getTime, formatSeparatorTime } = useDateTime()
const { onScroll, scrollToBottom } = useScroll()

const friendProfileModal = ref()

const scrollContainer = ref<HTMLElement | null>(null)
const showScrollButton = ref(false)

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

        const isSamePrev = prev && prev.sender.id === msg.sender.id
        const isSameNext = next && next.sender.id === msg.sender.id

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

const scrollMore = () => {
    // if (messageStorage.isLoading || !messageStorage.hasMore) return

    // checkScrollAtBottom()

    // onScroll(scrollContainer.value, () => messageStorage.getMessages(conversationStorage.conversation!.id))

    checkScrollAtBottom()

    // Chỉ gọi load more khi gần đầu trang và còn dữ liệu
    if (messageStorage.hasMore && !messageStorage.isLoading) {
        const { scrollTop } = scrollContainer.value!
        if (scrollTop < 300) {   // gần đầu
            onScroll(scrollContainer.value, () =>
                messageStorage.getMessages(conversationStorage.conversation!.id)
            )
        }
    }
}

const checkScrollAtBottom = () => {
    if (!scrollContainer.value) return
    const { scrollTop, scrollHeight, clientHeight } = scrollContainer.value
    const distanceFromBottom = scrollHeight - scrollTop - clientHeight

    showScrollButton.value = distanceFromBottom > 200
}

const handleScrollBottom = (smooth: boolean) => {
    scrollToBottom(scrollContainer.value!, smooth)
    // Đợi scroll hoàn tất rồi mới ẩn nút
    setTimeout(() => {
        showScrollButton.value = false
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
    await messageStorage.getMessages(conversationStorage.conversation!.id)

    await nextTick()
    await waitForImages()
    
    handleScrollBottom(false)
    console.log('123')
}

onMounted(async () => {
    reset()
})

watch(() => conversationStorage.conversation, async () => {
    reset()
})
</script>