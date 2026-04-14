<template>
    <div ref="el" :class="[
        'text-sm flex flex-col relative min-w-12 rounded-lg',
        'px-2 py-2',
        isOwner && message.content ? 'bg-blue-400 text-white' : !isOwner && message.content ? 'bg-white dark:bg-gray-800 dark:text-slate-100' : '',
        (role && role != MemberRoleEnum.MEMBER && message.attachments?.length <= 1 && !isOwner)
            ? 'border-blue-500 border' // Nếu có role đặc biệt
            : (isOwner ? 'border-blue-500' : 'border-slate-300 dark:border-gray-700') // Border mặc định
    ]" :data-id="message.id" class="max-w-70 md:max-w-105 flex flex-col gap-2 cursor-pointer">

        <!-- USERNAME -->
        <span v-if="message.isFirst && !isOwner" class="text-xs text-slate-400 dark:text-slate-300">
            {{ message.sender?.username }}
        </span>

        <div v-for="(file, index) in getDocuments(message.attachments)" :key="'doc-' + index"
            class="flex flex-col items-center gap-3 p-3 rounded-lg border bg-gray-50 dark:bg-gray-900/50 border-gray-200 dark:border-gray-700 hover:bg-gray-100 dark:hover:bg-gray-800 transition shadow-sm">

            <div class="flex w-full gap-2">
                <div class="text-2xl md:text-3xl">
                    {{ getFileIcon(file.secureUrl) }}
                </div>

                <div class="flex-1 min-w-0">
                    <p class="text-xs md:text-sm font-medium truncate"
                        :class="isOwner ? 'text-gray-900 dark:text-gray-100' : 'text-gray-800 dark:text-slate-200'">
                        {{ file.name || 'Untitled File' }}
                    </p>
                    <p class="text-[10px] text-gray-500 uppercase">{{ getFileExt(file.secureUrl) }}</p>
                </div>

                <div class="flex">
                    <a :href="file.secureUrl" target="_blank" v-if="getFileExt(file.secureUrl) == 'PDF'"
                        class="p-2 rounded-full bg-yellow-500 hover:bg-yellow-600 text-white transition-transform active:scale-90">
                        <i class="fa-solid fa-folder text-yellow-500"></i>
                    </a>

                    <a :href="file.secureUrl.replace('/upload/', '/upload/fl_attachment/')" download
                        class="p-2 rounded-full bg-blue-500 hover:bg-blue-600 text-white transition-transform active:scale-90">
                        <i class="fa-solid fa-download"></i>
                    </a>
                </div>
            </div>

            <!-- TIME -->
            <span v-if="message.showTime" :class="[
                'text-[10px] md:text-xs left-0 w-full',
                style.text.secondary
            ]">
                {{ formatTime(message.ct) }}
            </span>

        </div>

        <Reactions :message="message" />
    </div>
</template>
<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';
import Reactions from '../Reaction/Reactions.vue';
import { MemberRoleEnum } from '@/types/enum';
import { MediaType } from '@/types/entities';
import { style } from '@/assets/tailwindcss';
import { useDateTime } from '@/composables/useDateTime';

const props = defineProps<{
    setBubbleRef?: (el: HTMLElement | null) => void
    message: any
    isOwner: boolean
    role?: MemberRoleEnum;
}>()

const el = ref<HTMLElement | null>(null)
const { formatTime } = useDateTime()

// Các hàm helper để lọc loại file
const isImage = (url: string) => /\.(jpg|jpeg|png|webp|gif|avif)$/i.test(url);

const getDocuments = (attachments: MediaType[]) => attachments?.filter(a => !isImage(a.secureUrl));

const getFileExt = (fileName: string) => fileName.split('.').pop()?.toUpperCase() || 'FILE';

const getFileIcon = (fileName: string) => {
    const ext = fileName.split('.').pop()?.toLowerCase();
    switch (ext) {
        case 'doc': case 'docx': return '📄';
        case 'xls': case 'xlsx': return '📊';
        case 'ppt': case 'pptx': return '📽️';
        case 'pdf': return '📕';
        case 'zip': case 'rar': return '📦';
        default: return '📁';
    }
};
console.log(props.message)
onMounted(() => {
    props.setBubbleRef?.(el.value ?? null)
})
onBeforeUnmount(() => {
    props.setBubbleRef?.(null)
})
</script>