<template>
    <div class="flex items-center">
        <div class="flex items-center">
            <CircleAvatar v-if="!isMeAction" :user="msg.sender" size="w-6 h-6 mr-1" :is-disabled="true" />
            <span @click="openProfile(msg.sender)"
                class="font-bold cursor-pointer hover:underline text-gray-700 dark:text-gray-200">
                {{ isMeAction ? t("you") : msg.sender?.username }}
            </span>
        </div>

        <span class="mx-1">{{ t(msg.content) }}</span>
        <span @click="openProfile(msg.systemMetadata.user)"
            class="flex items-center font-bold cursor-pointer hover:underline text-gray-700 dark:text-gray-200">
            <CircleAvatar v-if="msg.systemMetadata.user?.id != userStorage.user?.id" :user="msg.systemMetadata.user"
                size="w-6 h-6 mr-1" :is-disabled="true" />
            {{ msg.systemMetadata.user?.id === userStorage.user?.id ? t("you") : msg.systemMetadata.user?.username }}
        </span>
    </div>
</template>

<script setup lang="ts">
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import { useTranslate } from '@/composables/useTranslate';
import { useUserStore } from '@/stores/user.storage';
import { MessageType, UserType } from '@/types/entities';

const props = defineProps<{
    msg: MessageType
    openProfile: (user: UserType) => void
    isMeAction: boolean
}>()

const { t } = useTranslate()
const userStorage = useUserStore()
</script>