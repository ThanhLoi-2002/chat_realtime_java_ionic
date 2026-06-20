<template>
    <div ref="el" :class="[
        'text-sm flex flex-col relative min-w-12 rounded-lg w-full',
        'px-2 py-1',
    ]" :data-id="message.id" class="w-full flex flex-col gap-2 cursor-pointer">
        <a :href="message.linkMetadata.url" target="_blank"
            class="flex flex-col items-center gap-3 p-2 w-full rounded-lg border bg-gray-50 dark:bg-gray-900/50 border-gray-200 dark:border-gray-700 hover:bg-gray-100 dark:hover:bg-gray-800 transition shadow-sm">

            <div class="flex items-center w-full gap-2">
                <div class="text-2xl md:text-3xl">
                    <img :src="message.linkMetadata.image" class="w-14 h-14 rounded-md"/>
                </div>

                <div class="flex-1 min-w-0 flex flex-col gap-1">
                    <p class="text-xs md:text-sm font-medium truncate" :class="'text-gray-800 dark:text-slate-200'">
                        {{ message.linkMetadata.title || 'Untitled' }}
                    </p>
                    <div class="flex items-center gap-1 mt-1.5">
                        <i class="fa-solid fa-link text-[8px] opacity-70"></i>
                        <span class="text-[10px] uppercase font-bold tracking-wider opacity-70">
                            {{ formatHostname(message.linkMetadata.url) }}
                        </span>
                    </div>
                    <!-- TIME -->
                    <span :class="[
                        'text-[10px] md:text-xs left-0 w-full',
                        style.text.secondary
                    ]">
                        {{ formatSeparatorTime(message.ct) }}
                    </span>
                </div>
            </div>
        </a>
    </div>
</template>
<script setup lang="ts">
import { MessageType } from '@/types/entities';
import { style } from '@/assets/tailwindcss';
import { useDateTime } from '@/composables/useDateTime';
import { useMessage } from '@/composables/useMessage';

const props = defineProps<{
    message: MessageType
}>()

const { formatSeparatorTime } = useDateTime()
const { formatHostname } = useMessage()
</script>