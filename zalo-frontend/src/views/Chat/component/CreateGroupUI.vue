<template>
    <div class="flex flex-col h-[90%] bg-white dark:bg-gray-900 text-gray-800 dark:text-gray-100">
        <!-- BODY -->
        <div class="flex-1 overflow-hidden flex flex-col px-4 py-3 rounded-lg">

            <!-- GROUP NAME -->
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full bg-gray-200 dark:bg-gray-700 flex items-center justify-center">
                    <i class="fa fa-camera"></i>
                </div>

                <input placeholder="Nhập tên nhóm..." class="flex-1 border-b bg-transparent outline-none
                 border-gray-300 dark:border-gray-600
                 pb-1" />
            </div>

            <!-- SEARCH -->
            <div class="mt-3">
                <input placeholder="Nhập tên, số điện thoại..." class="w-full px-4 py-2 rounded-full
                 bg-gray-100 dark:bg-gray-800
                 outline-none" />
            </div>
            <!-- LIST -->
            <div class="flex-1 mt-4 pr-1">

                <p class="text-sm text-gray-500 mb-2">{{ t("recentChat") }}</p>

                <div class="flex gap-2">
                    <!-- Recent users list: cố định chiều cao, scroll riêng -->
                    <div class="flex-1">
                        <div class="h-[70%] overflow-y-auto bg-white dark:bg-gray-900 rounded">
                            <div v-for="user in users" :key="user.id"
                                class="flex items-center gap-3 py-2 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800 rounded px-2"
                                @click="selectUser(user)">
                                <span class="w-4 h-4 rounded-full flex justify-center items-center border-slate-400 border"
                                    :class="isSelected(user) ? 'bg-blue-400' : 'dark:bg-slate-600'"><i
                                        class="fa fa-check" v-show="isSelected(user)" /></span>
                                <img :src="user.avatar" class="w-10 h-10 rounded-full object-cover" />
                                <span class="truncate text-base">{{ user.name }}</span>
                            </div>
                        </div>
                    </div>

                    <!-- Selected users list: cũng có scroll riêng; ẩn nếu rỗng -->
                    <div v-if="selectedUsers.length > 0" class="w-40">
                        <span class="dark:text-slate-400 text-sm">{{ t('selected') }} {{ selectedUsers.length
                            }}/100</span>
                        <div class="h-[70%] overflow-y-auto bg-white dark:bg-gray-900 rounded flex flex-col gap-2 mt-2">
                            <div v-for="user in selectedUsers" :key="user.id"
                                class="flex items-center justify-between gap-2 py-1 cursor-pointer bg-blue-500 rounded-full px-2">
                                <div class="flex gap-2 items-center">
                                    <img :src="user.avatar" class="w-6 h-6 rounded-full object-cover" />
                                    <span class="truncate text-sm">{{ user.name }}</span>
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
            <ion-button class="rounded cursor-pointer" :disabled="selectedUsers.length < 2">
                {{ t("createGroup") }}
            </ion-button>
        </div>

    </div>
</template>

<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { inject, ref } from 'vue';

const dismiss = inject<() => void>("modalDismiss")
const { t } = useTranslate()
const users = ref<any[]>([
    { id: 1, name: 'John kt', avatar: 'https://i.pravatar.cc/100?img=1' },
    { id: 2, name: 'Mỹ Hoàng', avatar: 'https://i.pravatar.cc/100?img=2' },
    { id: 3, name: 'Ho Phat', avatar: 'https://i.pravatar.cc/100?img=3' },
    { id: 4, name: 'Doantbphuong', avatar: 'https://i.pravatar.cc/100?img=4' },
    { id: 5, name: 'Mark', avatar: 'https://i.pravatar.cc/100?img=5' },
    { id: 5, name: 'Mark', avatar: 'https://i.pravatar.cc/100?img=5' },
    { id: 5, name: 'Mark', avatar: 'https://i.pravatar.cc/100?img=5' },
    { id: 5, name: 'Mark', avatar: 'https://i.pravatar.cc/100?img=5' },
    { id: 5, name: 'Mark', avatar: 'https://i.pravatar.cc/100?img=5' },
    { id: 5, name: 'Mark', avatar: 'https://i.pravatar.cc/100?img=5' },
    { id: 5, name: 'Mark', avatar: 'https://i.pravatar.cc/100?img=5' },
    { id: 5, name: 'Mark', avatar: 'https://i.pravatar.cc/100?img=5' },
])

const selectedUsers = ref<any[]>([
])

const isSelected = (user: any) => {
    return selectedUsers.value.some(u => u.id === user.id)
}

const selectUser = (user: any) => {
    const exists = isSelected(user)
    if (!exists) {
        selectedUsers.value.push(user)
    } else {
        removeUser(user)
    }
}

const removeUser = (user: any) => {
    selectedUsers.value = selectedUsers.value.filter(u => u.id !== user.id)
}
</script>