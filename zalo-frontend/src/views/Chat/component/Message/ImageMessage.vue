<template>
    <div ref="el" :class="[
        'text-sm flex flex-col relative min-w-12 rounded-lg',
        'px-2 py-2',
        isOwner && message.content ? 'bg-blue-400 text-white' : !isOwner && message.content ? 'bg-white dark:bg-gray-800 dark:text-slate-100' : '',
        (role && role != MemberRoleEnum.MEMBER && message.attachments?.length <= 1 && !isOwner)
            ? 'border-blue-500 border' // Nếu có role đặc biệt
            : (isOwner ? 'border-blue-500' : 'border-slate-300 dark:border-gray-700') // Border mặc định
    ]" :data-id="message.id">
        <!-- USERNAME -->
        <span v-if="message.isFirst && !isOwner" class="text-xs text-slate-400 dark:text-slate-300">
            {{ message.sender?.username }}
        </span>

        <!-- CONTENT -->
        <div :class="[
            message.showTime ? 'py-1' : 'py-0.5',
            'max-w-62.5 md:max-w-100 flex flex-col gap-2'

        ]">
            <div v-if="message.stt === -1"
                class="flex flex-col justify-center items-center w-20 h-20 md:w-30 md:h-30 bg-slate-300 dark:bg-slate-500 rounded-xl cursor-pointer hover:opacity-90 transition">
                <i class="fa-solid fa-image text-6xl md:text-8xl"></i>
                <span class="italic text-gray-700 dark:text-gray-800 text-[10px] md:text-sm">
                    {{ t('deleted') }}
                </span>
            </div>

            <template v-else>
                <div v-if="message.attachments?.length > 0" class="max-w-70 md:max-w-105">
                    <div class="flex flex-wrap gap-1 rounded-xl overflow-hidden border border-black/5 shadow-sm">
                        <div v-for="(img, index) in message.attachments" :key="index"
                            class="relative group cursor-pointer overflow-hidden rounded" :class="[
                                'flex-auto h-25 md:h-32.5 min-w-[30%] aspect-auto',
                                message.attachments.length === 1 ? 'rounded-xl h-auto max-h-80 aspect-auto' : ''
                            ]">

                            <div v-if="isVideo(img.secureUrl)" class="relative h-full w-full" @click="handlePreviewImage(message, +index)">
                                <img :src="img.secureUrl.replace(/\.[^/.]+$/, '.jpg')"
                                    class="w-full h-full object-cover opacity-80" />
                                <div class="absolute inset-0 flex items-center justify-center">
                                    <i class="fa-solid fa-circle-play text-white text-3xl shadow-lg"></i>
                                </div>
                            </div>

                            <img v-else :src="img.secureUrl" @click="handlePreviewImage(message, +index)"
                                class="w-full h-full object-cover" />
                        </div>
                    </div>
                </div>

                <div v-if="message.content" :class="[
                    'wrap-break-word leading-relaxed px-1',
                    isOwner ? 'text-white' : 'text-slate-800 dark:text-slate-200'
                ]">
                    {{ message.content }}
                </div>
            </template>
        </div>

        <!-- TIME -->
        <span v-if="message.showTime" :class="[
            'text-[10px] md:text-xs',
            style.text.secondary
        ]">
            {{ formatTime(message.ct) }}
        </span>
    </div>

    <Reactions :message="message" />
</template>
<script setup lang="ts">
import { useDateTime } from '@/composables/useDateTime';
import { useTranslate } from '@/composables/useTranslate';
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { style } from '@/assets/tailwindcss';
import { useMessageStore } from '@/stores/message.storage';
import Reactions from '../Reaction/Reactions.vue';
import { MediaType, MessageType } from '@/types/entities';
import { MemberRoleEnum } from '@/types/enum';
import { useMedia } from '@/composables/useMedia';

const props = defineProps<{
    setBubbleRef?: (el: HTMLElement | null) => void
    message: any
    isOwner: boolean
    role?: MemberRoleEnum;
}>()

const { isVideo } = useMedia()
const { formatTime } = useDateTime()
const messStorage = useMessageStore()
const { t } = useTranslate()

const el = ref<HTMLElement | null>(null)

// Sửa lại hàm xử lý click
const handlePreviewImage = (mess: MessageType, index: number) => {
    const media: MediaType = {
        ...mess.attachments[index],
        createdBy: mess.sender,
        messageContent: mess.content
    }
    // Truyền cả message và index của ảnh được chọn
    messStorage.setPreviewImage(media)
}

onMounted(() => {
    props.setBubbleRef?.(el.value ?? null)
})
onBeforeUnmount(() => {
    props.setBubbleRef?.(null)
})
</script>