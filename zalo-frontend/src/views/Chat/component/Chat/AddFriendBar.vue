<template>
    <!-- HEADER ACTION -->
    <div v-if="(friendshipLocal?.status == FriendshipStatusEnum.PENDING || !friendshipLocal) && !isGroup(conversationStorage.conversation)"
        class="absolute top-1 left-1/2 -translate-x-1/2
        px-4 py-2 flex justify-between items-center
        bg-gray-100 dark:bg-slate-700 border
        border-slate-300 dark:border-slate-800
        z-10 rounded-sm shadow min-w-[98%]
        text-xs md:text-base" :class="[style.text.primary]">
        <!-- TEXT -->
        <div class="flex gap-2 items-center">
            <i class="fa-solid fa-user-plus"></i>

            <span class="text-sm">
                {{
                    friendshipLocal?.status == FriendshipStatusEnum.PENDING
                        ? isSender
                            ? `${t("you")} ${t("haveSentAddFriendRequest")}`
                            : `${username} ${t("haveSentAddFriendRequest")}`
                        : t("sendFriendRequest")
                }}
            </span>
        </div>

        <!-- ACTION -->
        <div class="flex gap-2 items-center">

            <!-- 👉 CASE: PENDING -->
            <template v-if="friendshipLocal?.status == FriendshipStatusEnum.PENDING">

                <!-- ✅ NGƯỜI GỬI -->
                <template v-if="isSender">
                    <div class="px-3 py-1 text-sm rounded-sm cursor-pointer bg-gray-300 dark:bg-slate-600 hover:bg-gray-400"
                        @click="showConfirmCancel = true">
                        {{ t("cancleAddFriend") }}
                    </div>
                </template>

                <!-- ✅ NGƯỜI NHẬN -->
                <template v-else>
                    <div class="px-3 py-1 text-sm rounded-sm cursor-pointer bg-blue-500 text-white hover:bg-blue-600"
                        @click="showConfirmAccept = true">
                        {{ t("accept") }}
                    </div>

                    <div class="px-3 py-1 text-sm rounded-sm cursor-pointer bg-gray-300 dark:bg-slate-600 hover:bg-gray-400"
                        @click="showConfirmReject = true">
                        {{ t("reject") }}
                    </div>
                </template>
            </template>

            <!-- 👉 CASE: CHƯA KẾT BẠN -->
            <template v-else>
                <div class="px-3 py-1 text-sm rounded-sm cursor-pointer bg-gray-300 dark:bg-slate-600 hover:bg-gray-400"
                    @click="openModal">
                    {{ t("addFriend") }}
                </div>
            </template>
        </div>
    </div>

    <!-- MODAL -->
    <Modal ref="modalRef" :title="t(pageModal == 'friendProfile' ? 'profile' : pageModal)"
        :go-back="() => goPage('addFriend')" :isDisplayBackButton="pageModal != Object.keys(pages)[0]">
        <transition name="slide" mode="out-in">
            <KeepAlive>
                <component :is="pages[pageModal]" :key="pageModal" :goPage="goPage" :user="user" />
            </KeepAlive>
        </transition>
    </Modal>

    <!-- CONFIRM: CANCEL -->
    <ConfirmModal v-model:showConfirm="showConfirmCancel" :onOk="cancelFriend" :message="t('confirmCancelRequest')"
        :header="t('cancleAddFriend')" />

    <!-- CONFIRM: REJECT -->
    <ConfirmModal v-model:showConfirm="showConfirmReject" :onOk="rejectFriend" :message="t('confirmRejectRequest')"
        :header="t('rejectAddFriend')" />

    <ConfirmModal v-model:showConfirm="showConfirmAccept" :onOk="acceptFriend" :message="t('confirmAcceptRequest')"
        :header="t('acceptFriend')" />
</template>

<script setup lang="ts">
import { style } from '@/assets/tailwindcss'
import Modal from '@/components/Modal/Modal.vue'
import { useTranslate } from '@/composables/useTranslate'
import AddFriendRequestUI from '@/views/Friend/component/AddFriendRequestUI.vue'
import FriendProfileUI from '../FriendProfileUI.vue'
import { computed, ref, watch } from 'vue'
import { useConversation } from '@/composables/useConversation'
import { useConversationStore } from '@/stores/conversation.storage'
import { FriendshipType } from '@/types/entities'
import { FriendshipStatusEnum } from '@/types/enum'
import { useFriendshipStore } from '@/stores/friendship.storage'
import { useFriendship } from '@/composables/useFriendship'
import { useUserStore } from '@/stores/user.storage'

const { t } = useTranslate()
const { getRecipient, isGroup } = useConversation()
const { getActionUser } = useFriendship()

const conversationStorage = useConversationStore()
const friendshipStorage = useFriendshipStore()
const userStorage = useUserStore()

const modalRef = ref()
const friendshipLocal = ref<FriendshipType | undefined>(undefined)
const pageModal = ref<'addFriend' | 'friendProfile'>('addFriend')

const showConfirmCancel = ref(false)
const showConfirmReject = ref(false)
const showConfirmAccept = ref(false)

const username = ref('')

/* ===================== COMPUTED ===================== */

const user = computed(() => {
    return getRecipient(conversationStorage.conversation)
})

const isSender = computed(() => {
    return getActionUser(friendshipLocal.value)?.id === userStorage.user?.id
})

/* ===================== ACTION ===================== */

const openModal = () => {
    goPage('addFriend')
    modalRef.value?.present()
}

const goPage = (page: 'addFriend' | 'friendProfile') => {
    pageModal.value = page
}

const acceptFriend = async () => {
    if (!friendshipLocal.value) return
    const isSuccess = await friendshipStorage.accept(user.value!.id!)

    if (isSuccess)
        friendshipLocal.value = { ...friendshipLocal.value, status: FriendshipStatusEnum.ACCEPTED }
}

const rejectFriend = async () => {
    if (!friendshipLocal.value) return
    const isSuccess = await friendshipStorage.reject(user.value!.id!)

    if (isSuccess)
        friendshipLocal.value = undefined
}

const cancelFriend = async () => {
    const recipient = getRecipient(conversationStorage.conversation)
    if (!recipient) return
    const isSuccess = await friendshipStorage.cancel(recipient.id)

    if (isSuccess)
        friendshipLocal.value = undefined
}

const getFriend = async () => {
    friendshipLocal.value = await friendshipStorage.getFriend(getRecipient(conversationStorage.conversation)?.id || 0)
}

/* ===================== WATCH ===================== */

watch(
    () => conversationStorage.conversation?.id,
    async () => {
        if (!isGroup(conversationStorage.conversation)) {
            await getFriend()
        }
    },
    { immediate: true }
)

//watch accepted from modal
watch(
    () => friendshipStorage.friendshipAccepted,
    async () => {
        if (!isGroup(conversationStorage.conversation) && friendshipStorage.friendshipAccepted) {
            friendshipLocal.value = friendshipStorage.friendshipAccepted
        }
    },
    { immediate: true }
)

watch(() => friendshipLocal.value, async () => {
    if (!isGroup(conversationStorage.conversation)) {
        if (friendshipLocal.value) {
            const actionUser = getActionUser(friendshipLocal.value)
            username.value =
                actionUser?.id === userStorage.user?.id
                    ? t('you')
                    : actionUser?.username ?? ''
        } else {
            username.value = ''
        }
    }

}, { immediate: true })

/* ===================== PAGES ===================== */

const pages = {
    addFriend: AddFriendRequestUI,
    friendProfile: FriendProfileUI
}
</script>