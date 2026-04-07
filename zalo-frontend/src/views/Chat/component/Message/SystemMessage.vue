<template>
    <div class="flex justify-center my-3 px-4">
        <div class="bg-gray-100 dark:bg-slate-800 px-4 py-1.5 rounded-full border border-gray-200 dark:border-slate-700 max-w-[90%] shadow-sm">
            <p class="text-[12px] text-gray-500 dark:text-gray-400 text-center">
                
                <template v-if="systemType === 'ADD_USERS_TO_GROUP'">
                    <span class="font-bold text-gray-700 dark:text-gray-200">
                        {{ isMeAction ? t('You') : msg.sender?.username }}
                    </span>
                    <span class="mx-1">{{ t('added') }}</span>
                    <span class="font-bold text-gray-700 dark:text-gray-200">{{ formatAddedUsers }}</span>
                    <span class="ml-1">{{ t('to the group') }}</span>
                </template>

                <template v-else-if="systemType === 'LEAVE_GROUP' || systemType === 'REMOVE_MEMBER'">
                    <span class="font-medium italic">
                        {{ leaveMessage }}
                    </span>
                </template>

                <template v-else>
                    {{ t(msg.content) }}
                </template>

            </p>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useTranslate } from '@/composables/useTranslate';
import { useUserStore } from '@/stores/user.storage';

const props = defineProps<{
    msg: any 
}>();

const { t } = useTranslate();
const userStorage = useUserStore();
const myId = userStorage.user?.id;

// 1. Phân loại loại tin nhắn hệ thống
const systemType = computed(() => props.msg.systemMetadata?.type);

// 2. Kiểm tra mình có phải người thực hiện hành động (Sender) không
const isMeAction = computed(() => props.msg.sender?.id === myId);

// 3. Logic xử lý thông báo Rời nhóm / Mời ra khỏi nhóm
const leaveMessage = computed(() => {
    // Giả sử sender là người thực hiện hành động
    // Nếu sender.id == userId bị xóa => Tự rời
    // Nếu sender.id != userId bị xóa => Bị mời ra
    const targetUser = props.msg.sender; // Object user bị tác động
    const isTargetMe = targetUser?.id === myId;

    if (isMeAction.value) {
        // Mình là người thực hiện
        return isTargetMe ? t('You left the group') : `${t('You removed')} ${targetUser?.username}`;
    } else {
        // Người khác thực hiện
        return isTargetMe ? t('You were removed from the group') : `${targetUser?.username} ${t('left the group')}`;
    }
});

// 4. Logic xử lý danh sách người được thêm (Giữ nguyên như cũ nhưng thêm check "bạn")
const formatAddedUsers = computed(() => {
    const users = props.msg.systemMetadata?.addedUsersToGroup || [];
    const names = users.map((u: any) => u.id === myId ? t('you') : u.username);

    if (names.length === 0) return "";
    if (names.length === 1) return names[0];
    
    if (names.length <= 3) {
        const last = names.pop();
        return `${names.join(', ')} ${t('and')} ${last}`;
    } else {
        const firstTwo = names.slice(0, 2).join(', ');
        return `${firstTwo} ${t('and')} ${names.length - 2} ${t('others')}`;
    }
});
</script>
