<template>
    <div class="flex items-center gap-2 p-2 w-full max-w-full bg-gray-200/50 dark:bg-gray-800/50 border-l-4 border-blue-500 rounded-t-lg cursor-pointer hover:bg-gray-300 dark:hover:bg-gray-700 transition-colors overflow-hidden"
        @click="goToReplyingMessage">

        <div v-if="hasMedia"
            class="size-10 rounded overflow-hidden shrink-0 bg-gray-300 dark:bg-gray-600 flex items-center justify-center">
            <img v-if="isImage" :src="replyingMessage.attachments?.[0]?.secureUrl" class="size-full object-cover" />
            <div v-else-if="isVideo" class="relative size-full flex items-center justify-center bg-black">
                <i class="fa-solid fa-play text-white text-[10px]"></i>
            </div>
            <i v-else-if="isFile" class="fa-solid fa-file-lines text-blue-500 text-sm"></i>
        </div>

        <div class="flex-1 min-w-0 flex flex-col justify-center">
            <div class="flex items-center gap-2 w-full">
                <p class="text-[11px] text-blue-500 font-bold flex items-center gap-1 truncate">
                    <i class="fa-solid fa-reply scale-x-[-1] shrink-0"></i>
                    <span class="truncate">
                        {{ t('replyingTo') }}
                        {{ userStorage.user?.id === replyingMessage.sender.id ? t('you') : replyingMessage.sender.username }}
                    </span>
                </p>
                <span v-if="replyingMessage.stt === -1" class="text-[10px] italic text-gray-400 shrink-0">
                    ({{ t('messageHasBeenWithdrawn') }})
                </span>
            </div>

            <div class="text-[13px] text-gray-600 dark:text-slate-400 min-w-0 flex-1">
                <span v-if="isImage" class="flex items-center gap-1 truncate">
                    <i class="fa-regular fa-image shrink-0"></i> {{ t('image') }}
                </span>
                <span v-else-if="isVideo" class="flex items-center gap-1 truncate">
                    <i class="fa-regular fa-circle-play shrink-0"></i> {{ t('video') }}
                </span>
                <span v-else-if="isFile" class="flex items-center gap-1 text-blue-400 italic truncate">
                    <i class="fa-solid fa-paperclip shrink-0"></i> {{ replyingMessage.attachments?.[0]?.name || t('file') }}
                </span>
                <p v-else v-html="formattedContent" class="truncate block w-full m-0 leading-tight overflow-hidden"></p>
            </div>
        </div>

        <button v-if="setReplyingMessage" @click.stop="setReplyingMessage(null)"
            class="size-6 shrink-0 flex items-center justify-center rounded-full hover:bg-gray-300 dark:hover:bg-gray-600 text-gray-500 transition-colors cursor-pointer">
            <i class="fa-solid fa-xmark text-sm"></i>
        </button>
    </div>
</template>
<script setup lang="ts">
import { useMessage } from '@/composables/useMessage';
import { useTranslate } from '@/composables/useTranslate';
import { useUserStore } from '@/stores/user.storage';
import { MessageType } from '@/types/entities';
import { MessageEnum } from '@/types/enum';
import { computed } from 'vue';

const props = defineProps<{
    replyingMessage: MessageType
    setReplyingMessage?: (m: MessageType | null) => void
}>()

const userStorage = useUserStore()
const { t } = useTranslate()
const { formattedContentWithTag, jumpToMessage } = useMessage()

const isImage = computed(() => props.replyingMessage.contentType === MessageEnum.IMAGE);
const isVideo = computed(() => props.replyingMessage.contentType === MessageEnum.VIDEO);
const isFile = computed(() => props.replyingMessage.contentType === MessageEnum.FILE);

// Kiểm tra xem có cần hiện thumbnail không
const hasMedia = computed(() => {
    return (isImage.value || isVideo.value || isFile.value) &&
        props.replyingMessage.attachments &&
        props.replyingMessage.attachments.length > 0;
});

const formattedContent = computed(() => {
    if (!props.replyingMessage?.content) return "";
    return formattedContentWithTag(props.replyingMessage.content, false);
});

const goToReplyingMessage = () => {
    console.log(props.replyingMessage.id)
    if (!props.replyingMessage.id) return

    jumpToMessage(props.replyingMessage.id)
}
</script>