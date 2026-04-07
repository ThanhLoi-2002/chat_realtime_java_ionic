<template>
    <div class="flex flex-col h-[90%] bg-white dark:bg-gray-900 text-gray-800 dark:text-gray-100">
        <!-- BODY -->
        <div class="flex-1 overflow-hidden flex flex-col px-4 py-3 rounded-lg h-full">

            <!-- SEARCH -->
            <div class="mt-3">
                <input placeholder="Nhập tên, số điện thoại..." class="w-full px-4 py-2 rounded-full
                 bg-gray-100 dark:bg-gray-800
                 outline-none" v-model="keyword" />
            </div>
            <!-- LIST -->
            <div class="flex-1 mt-4 pr-1 h-full">

                <!-- <p class="text-sm text-gray-500 mb-2">{{ t("recentChat") }}</p> -->

                <div class="flex gap-2 h-full">
                    <!-- Recent users list: cố định chiều cao, scroll riêng -->
                    <div class="flex-1">
                        <div class="h-[80%] overflow-y-auto bg-white dark:bg-gray-900 rounded">
                            <div v-for="(group, letter) in groupedFriends" :key="letter">
                                <!-- LETTER -->
                                <div class="text-gray-500 dark:text-gray-400 text-xs md:text-sm mb-2 mt-4">
                                    {{ letter }}
                                </div>

                                <!-- USERS -->
                                <div v-for="user in group" :key="user.id"
                                    class="flex items-center gap-3 py-2 rounded pl-2 transition" :class="[
                                        isAlreadyMember(user.id)
                                            ? 'opacity-50 cursor-not-allowed' // Style khi đã tham gia
                                            : 'cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800'
                                    ]" @click="!isAlreadyMember(user.id) && selectUser(user)">
                                    <span class="w-4 h-4 rounded-full flex justify-center items-center border" :class="[
                                        isAlreadyMember(user.id)
                                            ? 'bg-gray-400 border-gray-400' // Màu xám cho người đã vào group
                                            : isSelected(user)
                                                ? 'bg-blue-400 border-blue-400'
                                                : 'border-slate-400 dark:bg-slate-600'
                                    ]">
                                        <i class="fa fa-check text-[10px] text-white"
                                            v-show="isSelected(user) || isAlreadyMember(user.id)"></i>
                                    </span>

                                    <img :src="user.avatar.url" class="w-10 h-10 rounded-full object-cover" />

                                    <span class="truncate text-base">{{ user.username }}</span>
                                </div>
                            </div>

                        </div>
                    </div>

                    <!-- Selected users list: cũng có scroll riêng; ẩn nếu rỗng -->
                    <div v-if="selectedUsers.length > 0" class="w-40">
                        <span class="dark:text-slate-400 text-sm">{{ t('selected') }} {{ selectedUsers.length
                        }}/100</span>
                        <div class="h-[85%] overflow-y-auto bg-white dark:bg-gray-900 rounded flex flex-col gap-2 mt-2">
                            <div v-for="user in selectedUsers" :key="user.id"
                                class="flex items-center justify-between gap-2 py-1 cursor-pointer bg-blue-500 rounded-full px-2">
                                <div class="flex gap-2 items-center">
                                    <img :src="user.avatar.url" class="w-6 h-6 rounded-full object-cover" />
                                    <span class="truncate text-sm">{{ user.username }}</span>
                                </div>
                                <i class="fa fa-close mr-2 dark:hover:text-slate-300 text-sm"
                                    @click="removeUser(user)"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- FOOTER -->
        <div class="flex justify-end gap-3 px-4 py-3 border-t border-gray-200 dark:border-gray-700">
            <button
                class="px-4 py-2 bg-gray-200 hover:bg-slate-100 dark:hover:bg-slate-600 dark:bg-gray-700 rounded cursor-pointer"
                @click="dismiss">
                {{ t("cancel") }}
            </button>
            <ion-button class="rounded cursor-pointer" :disabled="selectedUsers.length < 1" @click="addMember">
                {{ t("addMember") }}
            </ion-button>
        </div>

    </div>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { useFriendshipStore } from '@/stores/friendship.storage';
import { UserType } from '@/types/entities';
import { normalizeText } from '@/utils/helper';
import { modalController } from '@ionic/vue';
import { computed, inject, onMounted, ref } from 'vue';

const dismiss = inject<() => void>("modalDismiss")
const friendshipStorage = useFriendshipStore()
const convStorage = useConversationStore()
const users = ref<UserType[]>(friendshipStorage.friends)

const keyword = ref("");

const { t } = useTranslate()
const selectedUsers = ref<UserType[]>([
])

const selectUser = (user: UserType) => {
    const exists = selectedUsers.value.some(u => u.id === user.id)
    if (!exists) {
        selectedUsers.value.push(user)
    } else {
        removeUser(user)
    }
}

const removeUser = (user: UserType) => {
    selectedUsers.value = selectedUsers.value.filter(u => u.id !== user.id)
}


const addMember = async () => {
    const success = await convStorage.addMembers(selectedUsers.value.map(u => u.id));

    if (success) {
        // đóng hết modal trước
        let topModal = await modalController.getTop()
        while (topModal) {
            await topModal.dismiss()
            topModal = await modalController.getTop()
        }
    }
}

// filter
const filtered = computed(() =>
    users.value.filter((f) =>
        normalizeText(f.username)
            .toLowerCase()
            .includes(normalizeText(keyword.value).toLowerCase()),
    ),
);

const groupedFriends = computed(() => {
    const groups: any = {};

    filtered.value.forEach((u) => {
        const letter = u.username.charAt(0).toUpperCase();
        if (!groups[letter]) groups[letter] = [];
        groups[letter].push(u);
    });

    return groups;
});

const isSelected = (user: UserType) => {
    return selectedUsers.value.some(u => u.id === user.id)
}

const isAlreadyMember = (userId: number) => {
    return convStorage.conversation?.members?.some(m => m.id === userId);
}

onMounted(async () => {
    if (friendshipStorage.friends.length == 0) {
        await friendshipStorage.getFriends()
        users.value = friendshipStorage.friends
    }
})
</script>