<template>
    <div class="flex items-center justify-center truncate">
        <i class="far fa-eye-slash text-orange-800 mr-2" :class="[isPinMessage ? 'fa-eye' : 'fa-eye-slash']"></i>

        <span @click="!isMeAction && openProfile(msg.sender)"
            class="font-bold cursor-pointer text-gray-700 dark:text-gray-200">
            {{ isMeAction ? t("you") : msg.sender?.username }}
        </span>

        <span class="mx-1 text-nowrap">{{ t(msg.content) }}</span>

        <span v-if="messType == MessageEnum.TEXT" class="text-gray-600 dark:text-gray-400 opacity-80 truncate">
            {{ msg.systemMetadata.message.content }}
        </span>

        <span v-if="messType == MessageEnum.IMAGE" class="text-gray-600 dark:text-gray-400 opacity-80 truncate">
            <img :src="msg.systemMetadata.message.attachments[0].secureUrl" class="w-6 h-5"/>
        </span>

        <span v-if="messType == MessageEnum.FILE" class="text-gray-600 dark:text-gray-400 opacity-80 truncate">
            {{ msg.systemMetadata.message.attachments[0].name }}.{{ extension }}
        </span>

        <span v-if="isPinMessage" class="ml-2 text-blue-500 cursor-pointer font-medium" @click="jumpToMessage(msg.id)">
            {{ t("see") }}
        </span>
    </div>
</template>
<script setup lang="ts">
import { useMessage } from '@/composables/useMessage';
import { useTranslate } from '@/composables/useTranslate';
import { MessageType, UserType } from '@/types/entities';
import { MessageEnum, SystemMetadataEnum } from '@/types/enum';

const props = defineProps<{
    msg: MessageType;
    isMeAction: boolean
}>()

const { t } = useTranslate();
const { jumpToMessage} = useMessage()

const isPinMessage = props.msg.systemMetadata.type === SystemMetadataEnum.PIN_MESSAGE
const messType = props.msg.systemMetadata.message.contentType
const extension = messType == MessageEnum.FILE || messType == MessageEnum.IMAGE ? props.msg.systemMetadata.message.attachments[0]?.secureUrl.split('.').pop() : '';

const openProfile = (user: UserType) => {
    console.log(user)
}
</script>