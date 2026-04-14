<template>
    <div ref="el" :class="[
        'text-sm flex flex-col relative min-w-12 rounded-lg w-full',
        'px-2 py-1',
    ]" :data-id="media.id" class="max-w-70 md:max-w-105 flex flex-col gap-2 cursor-pointer">
        <div
            class="flex flex-col items-center gap-3 p-3 w-full rounded-lg border bg-gray-50 dark:bg-gray-900/50 border-gray-200 dark:border-gray-700 hover:bg-gray-100 dark:hover:bg-gray-800 transition shadow-sm">

            <div class="flex items-center w-full gap-2">
                <div class="text-2xl md:text-3xl">
                    {{ getFileIcon() }}
                </div>

                <div class="flex-1 min-w-0 flex flex-col gap-1">
                    <p class="text-xs md:text-sm font-medium truncate" :class="'text-gray-800 dark:text-slate-200'">
                        {{ media.name || 'Untitled File' }}
                    </p>
                    <p class="text-[12px] uppercase" :class="[style.text.muted]">{{ formatBytesToMB(media.bytes) }} | {{
                        getFileExt() }}</p>
                    <!-- TIME -->
                    <span :class="[
                        'text-[10px] md:text-xs left-0 w-full',
                        style.text.secondary
                    ]">
                        {{ formatSeparatorTime(media.ct) }}
                    </span>
                </div>

                <div class="flex">
                    <a :href="media.secureUrl" target="_blank" v-if="getFileExt() == 'PDF'"
                        class="p-2 rounded-full bg-yellow-500 hover:bg-yellow-600 text-white transition-transform active:scale-90">
                        <i class="fa-solid fa-folder text-yellow-500"></i>
                    </a>

                    <a :href="media.secureUrl.replace('/upload/', '/upload/fl_attachment/')" download
                        class="p-2 rounded-full bg-blue-500 hover:bg-blue-600 text-white transition-transform active:scale-90">
                        <i class="fa-solid fa-download"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { MediaType } from '@/types/entities';
import { style } from '@/assets/tailwindcss';
import { useDateTime } from '@/composables/useDateTime';

const props = defineProps<{
    media: MediaType
}>()

const { formatSeparatorTime } = useDateTime()

// Các hàm helper để lọc loại file
const getFileExt = () => props.media.secureUrl.split('.').pop()?.toUpperCase() || 'FILE';

const getFileIcon = () => {
    const ext = props.media.secureUrl.split('.').pop()?.toLowerCase();
    switch (ext) {
        case 'doc': case 'docx': return '📄';
        case 'xls': case 'xlsx': return '📊';
        case 'ppt': case 'pptx': return '📽️';
        case 'pdf': return '📕';
        case 'zip': case 'rar': return '📦';
        default: return '📁';
    }
};

const formatBytesToMB = (bytes: number, decimals = 2) => {
    if (bytes === 0) return '0 MB';

    // Tính theo chuẩn 1024
    const res = bytes / (1024 * 1024);

    return parseFloat(res.toFixed(decimals)) + ' MB';
}
</script>