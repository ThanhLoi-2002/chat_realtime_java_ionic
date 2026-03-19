<template>
    <div class="px-4 bg-white dark:bg-gray-800 h-[85%] flex flex-col">
        <!-- Input -->
        <input v-model="phone" :placeholder="t('phone')"
            class="border-b p-2 outline-none bg-transparent dark:placeholder:text-slate-400 dark:text-slate-300" />

        <!-- Result -->
        <div class="overflow-y-auto mt-2 flex-1">
            <div v-if="usersRecent.length > 0">
                <div v-for="(user, index) in usersRecent" :key="index"
                    class="flex items-center justify-between gap-3 cursor-pointer hover:bg-blue-300 dark:hover:bg-blue-500 rounded-sm">
                    <div class="flex flex-1 gap-2 p-2" @click="() => selectUserRecent(user)">
                        <img :src="user?.avatar?.url ?? RANDOM_AVATAR" class="w-10 h-10 rounded-full" />
                        <div>
                            <p class="dark:text-slate-200">{{ user.username }}</p>
                            <p class="text-sm text-gray-700 dark:text-slate-300">{{ user.phone }}</p>
                        </div>
                    </div>

                    <i class="fa fa-close hover:bg-slate-200 dark:hover:bg-slate-700 p-1 rounded-sm dark:text-slate-300 mr-2"
                        @click="deleteUserRecent(user)"></i>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <div class="mt-4 flex justify-end gap-2">
            <button class="cursor-pointer dark:text-white" @click="() => dismiss?.()">{{ t('cancel') }}</button>
            <button @click="searchUser"
                class="bg-blue-600 hover:bg-blue-500 text-white px-4 py-2 rounded cursor-pointer">
                {{ t('search') }}
            </button>
        </div>
    </div>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useUserStore } from '@/stores/user.storage';
import { SearchFriendPageType } from '@/types/common';
import { UserType } from '@/types/entities';
import { RANDOM_AVATAR, SEARCH_USERS_RECENT } from '@/utils/constant';
import { getKey, setKey } from '@/utils/local';
import { inject, onMounted, ref } from 'vue';

const props = defineProps<{
    goPage: (page: SearchFriendPageType) => void
    setUser: (user?: UserType) => void
}>()

const userStorage = useUserStore()
const usersRecent = ref<UserType[]>([])
const phone = ref("")
const { t } = useTranslate()

const dismiss = inject<() => void>("modalDismiss")

const searchUser = async () => {
    if (!phone.value) return
    const data: UserType | undefined = await userStorage.searchByPhone(phone.value);
    if (data) {
        props.setUser(data)
        props.goPage('friendProfile')

        // Save local
        const usersRecent = getUsersRecent();
        const index = usersRecent.findIndex((u: UserType) => u.id === data.id)

        if (index !== -1) {
            // 🔥 đã tồn tại → update
            usersRecent[index] = data
        } else {
            // 🔥 chưa có → thêm mới (có thể push hoặc unshift)
            usersRecent.unshift(data) // thường thêm lên đầu
        }

        setKey(SEARCH_USERS_RECENT, usersRecent)
    }
}

const selectUserRecent = (user: UserType) => {
    props.setUser(user)
    props.goPage('friendProfile')
}

const deleteUserRecent = (user: UserType) => {
    usersRecent.value = usersRecent.value.filter(u => u.id !== user.id)
    setKey(SEARCH_USERS_RECENT, usersRecent.value)
}

const getUsersRecent = () => {
    return JSON.parse(getKey(SEARCH_USERS_RECENT) ?? '[]')
}

onMounted(() => {
    usersRecent.value = getUsersRecent()
})
</script>