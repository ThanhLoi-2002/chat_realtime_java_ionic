<template>
    <!-- HEADER -->
    <ChatHeader :isShowInfoSection="isShowInfoSection"
        @update:isShowInfoSection="val => emit('update:isShowInfoSection', val)" />

    <!-- MESSAGES -->
    <div ref="scrollContainer" class="flex-1 overflow-y-auto p-6 space-y-2" @scroll="scrollMore">
        <div v-if="messageStorage.isLoading" class="text-center text-gray-400 text-sm">
            <LoadingSpinner />
        </div>

        <div v-for="msg in messagesWithMeta" :key="msg.id">
            <!-- TIME SEPARATOR -->
            <div v-if="msg.showTimeSeparator" class="text-center text-xs text-gray-400 my-3">
                {{ formatSeparatorTime(msg.ct) }}
            </div>

            <MessageContainer :message="msg" :friendProfileModal="friendProfileModal" />
        </div>
    </div>

    <Modal ref="friendProfileModal" :title="t('profile')">
        <friend-profile-u-i :user="getRecipient(conversationStorage.conversation!)" />
    </Modal>

    <Typing :scrollContainer="scrollContainer" />
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { computed, onMounted, ref, watch } from 'vue';
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
    if (messageStorage.isLoading || !messageStorage.hasMore) return
    onScroll(scrollContainer.value, () => messageStorage.getMessages(conversationStorage.conversation!.id))
}

const reset = async () => {
    sysStorage.setShowBottomMenu(false)
    messageStorage.resetPagination()
    await messageStorage.getMessages(conversationStorage.conversation!.id)
    scrollToBottom(scrollContainer.value, false)
}

onMounted(async () => {
    reset()
})

watch(() => conversationStorage.conversation, async () => {
    reset()
})
</script>