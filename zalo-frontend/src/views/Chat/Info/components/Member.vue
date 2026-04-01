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
            <div class="flex items-center rounded-full px-3 py-2" :class="[style.bg.secondary, style.text.secondary]">
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

                <!-- AVATAR -->
                <CircleAvatar :user="user" />

                <!-- NAME -->
                <div class="flex-1 min-w-0">
                    <div class="truncate text-sm font-medium">
                        {{ userStorage.user?.id == user.id ? t("you") : user.username }}
                    </div>
                </div>

                <!-- ACTION -->
                <button v-if="!isFriend(user.id)" class="bg-blue-500 hover:bg-blue-600 text-xs px-3 py-1 rounded-md">
                    {{ t('addFriend') }}
                </button>
            </div>

        </div>
    </div>
</template>

<script setup lang="ts">
import { style } from '@/assets/tailwindcss'
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue'
import { useTranslate } from '@/composables/useTranslate'
import { useConversationStore } from '@/stores/conversation.storage'
import { useFriendshipStore } from '@/stores/friendship.storage'
import { useUserStore } from '@/stores/user.storage'
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
    isShowBackButton: boolean
}>()
const keyword = ref('')
const { t } = useTranslate()
const emit = defineEmits(['back'])
const convStorage = useConversationStore()
const friendStorage = useFriendshipStore()
const userStorage = useUserStore()

const filteredMembers = computed(() => {
    if (!keyword.value) return convStorage.conversation?.members

    return convStorage.conversation?.members.filter(u =>
        u.username.toLowerCase().includes(keyword.value.toLowerCase())
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