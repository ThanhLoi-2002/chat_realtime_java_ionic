<template>
    <div class="flex justify-center my-3 px-4">
        <div
            class="bg-gray-100 dark:bg-slate-800 px-4 py-1.5 rounded-full border border-gray-200 dark:border-slate-700 max-w-[90%] shadow-sm">
            <p class="text-[12px] text-gray-500 dark:text-gray-400 text-center leading-relaxed">

                <template v-if="systemType === SystemMetadataEnum.ADD_USERS_TO_GROUP">
                    <span @click="openProfile(msg.sender)"
                        class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                        {{ isMeAction ? t('you') : msg.sender?.username }}
                    </span>
                    <span class="mx-1">{{ t('added') }}</span>

                    <template v-for="(user) in formattedAddedList" :key="user.id">
                        <span v-if="user.isName" @click="openProfile(user.raw)"
                            class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                            {{ user.label }}
                        </span>
                        <span v-else>{{ user.label }}</span>
                    </template>

                    <span class="ml-1">{{ t('toTheGroup') }}</span>
                </template>

                <template v-else-if="systemType === SystemMetadataEnum.LEAVE_GROUP || systemType === SystemMetadataEnum.REMOVE_MEMBER">
                    <span class="italic">
                        <template v-if="isMeAction">
                            <span class="font-bold text-gray-700 dark:text-gray-200">{{ t('you') }}</span>
                            <template v-if="msg.sender?.id !== myId">
                                <span class="mx-1">{{ t('removed') }}</span>
                                <span @click="openProfile(msg.sender)"
                                    class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                                    {{ msg.sender?.username }}
                                </span>
                            </template>
                            <span v-else class="ml-1">{{ t('leftTheGroup') }}</span>
                        </template>

                        <template v-else>
                            <span @click="openProfile(msg.sender)"
                                class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                                {{ msg.sender?.username }}
                            </span>
                            <span class="ml-1">{{ t('leftTheGroup') }}</span>
                        </template>
                    </span>
                </template>

                <template v-else>
                    {{ t(msg.content) }}
                </template>
            </p>
        </div>

        <Modal ref="profileModal" :title="t('profile')">
            <FriendProfileUI :user="selectedUser" />
        </Modal>
    </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useTranslate } from '@/composables/useTranslate';
import { useUserStore } from '@/stores/user.storage';
import Modal from '@/components/Modal/Modal.vue';
import FriendProfileUI from '../FriendProfileUI.vue';
import { MessageType, UserType } from '@/types/entities';
import { SystemMetadataEnum } from '@/types/enum';

const props = defineProps<{
    msg: MessageType
}>();

const { t } = useTranslate();
const userStorage = useUserStore();
const myId = userStorage.user?.id;
const profileModal = ref()
const selectedUser = ref<UserType>()

// 1. Phân loại loại tin nhắn hệ thống
const systemType = computed(() => props.msg.systemMetadata?.type);

// 2. Kiểm tra mình có phải người thực hiện hành động (Sender) không
const isMeAction = computed(() => props.msg.sender?.id === myId);

// 3. Logic xử lý thông báo Rời nhóm / Mời ra khỏi nhóm
// const leaveMessage = computed(() => {
//     // Giả sử sender là người thực hiện hành động
//     // Nếu sender.id == userId bị xóa => Tự rời
//     // Nếu sender.id != userId bị xóa => Bị mời ra
//     const targetUser = props.msg.sender; // Object user bị tác động
//     const isTargetMe = targetUser?.id === myId;

//     if (isMeAction.value) {
//         // Mình là người thực hiện
//         return isTargetMe ? t('You left the group') : `${t('You removed')} ${targetUser?.username}`;
//     } else {
//         // Người khác thực hiện
//         return isTargetMe ? t('You were removed from the group') : `${targetUser?.username} ${t('left the group')}`;
//     }
// });

// Hàm mở Modal (Bạn thay bằng logic của dự án bạn, ví dụ: emit hoặc call store)
const openProfile = (user: UserType) => {
    if (!user || user.id === myId) return; // Không mở profile của chính mình (tùy bạn)
    selectedUser.value = user
    profileModal.value?.present()
};

// Logic xử lý danh sách người được thêm dưới dạng mảng các phần tử
const formattedAddedList = computed(() => {
    const users = props.msg.systemMetadata?.addedUsersToGroup || [];
    if (users.length === 0) return [];

    const result: any[] = [];

    if (users.length <= 3) {
        users.forEach((u: any, idx: number) => {
            result.push({
                isName: true,
                label: u.id === myId ? t('you') : u.username,
                raw: u
            });

            if (idx < users.length - 2) {
                result.push({ isName: false, label: ", " });
            } else if (idx === users.length - 2) {
                result.push({ isName: false, label: ` ${t('and')} ` });
            }
        });
    } else {
        // Trường hợp > 3 người: "User1, User2 và n người khác"
        const firstTwo = users.slice(0, 2);
        firstTwo.forEach((u: any, idx: number) => {
            result.push({
                isName: true,
                label: u.id === myId ? t('you') : u.username,
                raw: u
            });
            if (idx === 0) result.push({ isName: false, label: ", " });
        });

        result.push({ isName: false, label: ` ${t('and')} ${users.length - 2} ${t('others')}` });
    }

    return result;
});
</script>
