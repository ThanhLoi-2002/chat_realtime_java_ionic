<template>
    <div class="h-full flex flex-col overflow-auto">

        <!-- HEADER -->
        <div class="p-4 flex items-center gap-3 border-b dark:border-slate-700" :class="[style.text.secondary]">
            <i class="fa fa-arrow-left cursor-pointer" @click="$emit('back')" v-if="isShowBackButton"></i>
            <span class="font-semibold m-auto">{{ t("member") }}</span>
        </div>

        <!-- ADD MEMBER -->
        <div class="p-3">
            <div class="flex items-center justify-center gap-2 transition rounded-lg py-2 cursor-pointer"
                :class="[style.button.base]">
                <i class="fa fa-user-plus"></i>
                <span class="text-sm font-medium">{{ t("addMember") }}</span>
            </div>
        </div>

        <!-- HEADER -->
        <div class="px-3 flex items-center justify-between">
            <div class="text-sm font-medium" :class="[style.text.primary]">
                {{ t("memberList") }} ({{ convStorage.conversation?.members?.length }})
            </div>

            <i class="fa fa-ellipsis-h cursor-pointer" :class="[style.text.primary]"></i>
        </div>

        <!-- SEARCH -->
        <div class="p-3">
            <div
                :class="`flex items-center rounded-full px-3 py-2 border ${style.border.primary} ${style.bg.secondary} ${style.text.secondary}`">
                <i class="fa fa-search mr-2"></i>
                <input v-model="keyword" :placeholder="`${t('search')} ${t('member')}...`"
                    class="bg-transparent outline-none text-sm w-full placeholder-gray-400" />
            </div>
        </div>

        <!-- LIST -->
        <div class="flex-1 overflow-y-auto px-2 space-y-1 pb-4">

            <div v-for="user in filteredMembers" :key="user.id"
                class="flex items-center gap-3 p-2 rounded-lg transition cursor-pointer"
                :class="[style.text.secondary]">

                <div class="relative">
                    <!-- AVATAR -->
                    <CircleAvatar :user="user" />

                    <Key :role="user.role"/>
                </div>
                <!-- NAME -->
                <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-1.5">
                        <div class="truncate text-sm font-medium">
                            {{ userStorage.user?.id == user.id ? t("you") : user.username }}
                        </div>
                    </div>

                    <div v-if="user.addBy" class="text-xs opacity-60 truncate" :class="[style.text.secondary]">
                        {{ t('addedBy') }}: {{ user.addBy.username }}
                    </div>
                </div>

                <!-- ACTION -->
                <button v-if="!isFriend(user.id)" class="bg-blue-500 hover:bg-blue-600 text-xs px-3 py-1 rounded-md"
                    @click="openModal(user)">
                    {{ t('addFriend') }}
                </button>
            </div>

        </div>
    </div>

    <!-- MODAL -->
    <Modal ref="modalRef" :title="t(pageModal == 'friendProfile' ? 'profile' : pageModal)"
        :go-back="() => goPage('addFriend')" :isDisplayBackButton="pageModal != Object.keys(pages)[0]">
        <transition name="slide" mode="out-in">
            <KeepAlive>
                <component :is="pages[pageModal]" :key="pageModal" :goPage="goPage" :user="selectedUser" />
            </KeepAlive>
        </transition>
    </Modal>
</template>

<script setup lang="ts">
import { style } from '@/assets/tailwindcss'
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue'
import { useTranslate } from '@/composables/useTranslate'
import { useConversationStore } from '@/stores/conversation.storage'
import { useFriendshipStore } from '@/stores/friendship.storage'
import { useUserStore } from '@/stores/user.storage'
import { normalizeText } from '@/utils/helper'
import AddFriendRequestUI from '@/views/Friend/component/AddFriendRequestUI.vue'
import { computed, onMounted, ref } from 'vue'
import FriendProfileUI from '../../component/FriendProfileUI.vue'
import { UserType } from '@/types/entities'
import Key from '@/components/Key/Key.vue'

const props = defineProps<{
    isShowBackButton: boolean
}>()
const keyword = ref('')
const { t } = useTranslate()
const emit = defineEmits(['back'])
const convStorage = useConversationStore()
const friendStorage = useFriendshipStore()
const userStorage = useUserStore()

const modalRef = ref()
const pageModal = ref<'addFriend' | 'friendProfile'>('addFriend')
const pages = {
    addFriend: AddFriendRequestUI,
    friendProfile: FriendProfileUI
}
const selectedUser = ref<UserType | undefined>(undefined)

const openModal = (user: UserType) => {
    selectedUser.value = user
    goPage('addFriend')
    modalRef.value?.present()
}

const goPage = (page: 'addFriend' | 'friendProfile') => {
    pageModal.value = page
}

const filteredMembers = computed(() => {
    if (!keyword.value) return convStorage.conversation?.members

    return convStorage.conversation?.members.filter(u =>
        normalizeText(u.username).toLowerCase().includes(normalizeText(keyword.value).toLowerCase())
    )
})

const isFriend = (userId: number) => {
    return friendStorage.friends.some(f => f.id == userId) || userId == userStorage.user?.id
}

onMounted(() => {
    if (friendStorage.friends.length == 0) {
        friendStorage.getFriends()
    }
})
</script>