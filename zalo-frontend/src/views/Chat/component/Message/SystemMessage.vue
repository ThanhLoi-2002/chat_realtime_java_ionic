<template>
    <div class="">
        <img v-if="systemType === SystemMetadataEnum.UPDATE_GROUP_AVATAR && msg.systemMetadata?.groupAvatar"
            :src="msg.systemMetadata.groupAvatar.secureUrl" class="w-28 h-28 rounded-full mx-auto object-cover border"
            :class="[style.border.primary]" />
        <div class="flex justify-center mb-5 mt-2 px-4">
            <div
                class="bg-gray-100 dark:bg-slate-800 px-4 py-1.5 rounded-full border border-gray-200 dark:border-slate-700 max-w-[90%] shadow-sm">
                <p class="text-[12px] text-gray-500 dark:text-gray-400 text-center leading-relaxed">
                    <template v-if="systemType === SystemMetadataEnum.ADD_USERS_TO_GROUP">
                        <span @click="openProfile(msg.sender)"
                            class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                            {{ isMeAction ? t("you") : msg.sender?.username }}
                        </span>
                        <span class="mx-1">{{ t("added") }}</span>

                        <template v-for="user in formattedAddedList" :key="user.id">
                            <span v-if="user.isName" @click="openProfile(user.raw)"
                                class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                                {{ user.label }}
                            </span>
                            <span v-else>{{ user.label }}</span>
                        </template>

                        <span class="ml-1">{{ t("toTheGroup") }}</span>
                    </template>

                    <template v-else-if="
                        systemType === SystemMetadataEnum.LEAVE_GROUP
                    ">
                        <span class="italic flex items-center">
                            <CircleAvatar v-if="systemType === SystemMetadataEnum.LEAVE_GROUP" :user="msg.sender"
                                size="w-6 h-6 mr-1" />
                            <template v-if="isMeAction">
                                <span class="font-bold text-gray-700 dark:text-gray-200">{{
                                    t("you")
                                }}</span>
                                <template v-if="msg.sender?.id !== myId">
                                    <span class="mx-1">{{ t("removed") }}</span>
                                    <span @click="openProfile(msg.sender)"
                                        class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                                        {{ msg.sender?.username }}
                                    </span>
                                </template>
                                <span v-else class="ml-1">{{ t("leftTheGroup") }}</span>
                            </template>

                            <template v-else>
                                <span @click="openProfile(msg.sender)"
                                    class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                                    {{ msg.sender?.username }}
                                </span>
                                <span class="ml-1">{{ t("leftTheGroup") }}</span>
                            </template>
                        </span>
                    </template>

                    <template v-else-if="
                        systemType === SystemMetadataEnum.REMOVE_MEMBER
                    ">
                        <span class="italic flex items-center">
                            <template v-if="isMeAction">
                                <span class="font-bold text-gray-700 dark:text-gray-200">{{
                                    t("you")
                                }}</span>
                                <span class="mx-1">{{ t("haveRemoved") }}</span>
                                <CircleAvatar :user="msg.systemMetadata.user" size="w-6 h-6 mr-1" />
                                <span @click="openProfile(msg.systemMetadata.user)"
                                    class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                                    {{ msg.systemMetadata.user?.username }}
                                </span>
                                <span class="ml-1">{{ t("fromGroup") }}</span>
                            </template>

                            <template v-else>
                                <CircleAvatar :user="msg.sender" size="w-6 h-6 mr-1" />
                                <span @click="openProfile(msg.sender)"
                                    class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                                    {{ msg.sender?.username }}
                                </span>
                                <span class="ml-1">{{ t("haveRemovedYouFromGroup") }}</span>
                            </template>
                        </span>
                    </template>

                    <template v-else-if="systemType === SystemMetadataEnum.UPDATE_GROUP_NAME">
                        <span @click="openProfile(msg.sender)"
                            class="font-bold cursor-pointer hover:underline text-gray-700 dark:text-gray-200">
                            {{ isMeAction ? t("you") : msg.sender?.username }}
                        </span>
                        <span class="mx-1">{{ t(msg.content) }}</span>
                        <span class="font-bold text-gray-700 dark:text-gray-200">"{{ msg.systemMetadata?.groupName
                        }}"</span>
                    </template>

                    <template v-else-if="systemType === SystemMetadataEnum.UPDATE_GROUP_AVATAR">
                        <div class="items-center justify-center inline-flex">
                            <span @click="openProfile(msg.sender)"
                                class="font-bold cursor-pointer hover:underline text-gray-700 dark:text-gray-200">
                                {{ isMeAction ? t("you") : msg.sender?.username }}
                            </span>
                            <span class="mx-1">{{ t(msg.content) }}</span>
                        </div>
                    </template>

                    <template v-else-if="systemType === SystemMetadataEnum.CREATE_GROUP">
                        <span @click="openProfile(msg.sender)"
                            class="font-bold cursor-pointer hover:underline text-gray-700 dark:text-gray-200">
                            {{ isMeAction ? t("you") : msg.sender?.username }}
                        </span>
                        <span class="ml-1">{{ t(msg.content) }}</span>
                    </template>

                    <template v-else>
                        {{ t(msg.content) }}
                    </template>
                </p>
            </div>
        </div>

        <Modal ref="profileModal" :title="t('profile')">
            <FriendProfileUI :user="selectedUser" />
        </Modal>
    </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useTranslate } from "@/composables/useTranslate";
import { useUserStore } from "@/stores/user.storage";
import Modal from "@/components/Modal/Modal.vue";
import FriendProfileUI from "../FriendProfileUI.vue";
import { MessageType, UserType } from "@/types/entities";
import { SystemMetadataEnum } from "@/types/enum";
import { style } from "@/assets/tailwindcss";
import CircleAvatar from "@/components/Avatar/CircleAvatar.vue";

const props = defineProps<{
    msg: MessageType;
}>();

const { t } = useTranslate();
const userStorage = useUserStore();
const myId = userStorage.user?.id;
const profileModal = ref();
const selectedUser = ref<UserType>();

// 1. Phân loại loại tin nhắn hệ thống
const systemType = computed(() => props.msg.systemMetadata?.type);

// 2. Kiểm tra mình có phải người thực hiện hành động (Sender) không
const isMeAction = computed(() => props.msg.sender?.id === myId);

// Hàm mở Modal (Bạn thay bằng logic của dự án bạn, ví dụ: emit hoặc call store)
const openProfile = (user: UserType) => {
    if (!user || user.id === myId) return; // Không mở profile của chính mình (tùy bạn)
    selectedUser.value = user;
    profileModal.value?.present();
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
                label: u.id === myId ? t("you") : u.username,
                raw: u,
            });

            if (idx < users.length - 2) {
                result.push({ isName: false, label: ", " });
            } else if (idx === users.length - 2) {
                result.push({ isName: false, label: ` ${t("and")} ` });
            }
        });
    } else {
        // Trường hợp > 3 người: "User1, User2 và n người khác"
        const firstTwo = users.slice(0, 2);
        firstTwo.forEach((u: any, idx: number) => {
            result.push({
                isName: true,
                label: u.id === myId ? t("you") : u.username,
                raw: u,
            });
            if (idx === 0) result.push({ isName: false, label: ", " });
        });

        result.push({
            isName: false,
            label: ` ${t("and")} ${users.length - 2} ${t("others")}`,
        });
    }

    return result;
});
</script>
