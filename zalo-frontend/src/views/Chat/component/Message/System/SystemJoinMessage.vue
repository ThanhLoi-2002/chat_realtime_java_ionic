<template v-else-if="systemType === SystemMetadataEnum.LEAVE_GROUP">
    <span class="italic flex items-center">
        <CircleAvatar :user="msg.sender" size="w-6 h-6 mr-1" />
        <template v-if="isMeAction">
            <span class="font-bold text-gray-700 dark:text-gray-200">{{
                t("you")
            }}</span>
            <span class="ml-1">{{ t(msg.content) }}</span>
        </template>

        <template v-else>
            <span @click="openProfile(msg.sender)"
                class="font-bold text-gray-700 dark:text-gray-200 cursor-pointer hover:underline">
                {{ msg.sender?.username }}
            </span>
            <span class="ml-1">{{ t(msg.content) }}</span>
        </template>
    </span>
</template>

<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { MessageType, UserType } from '@/types/entities';

const props = defineProps < {
    msg: MessageType
    openProfile: (user: UserType) => void
    isMeAction: boolean
} > ()

const { t } = useTranslate()
</script>
