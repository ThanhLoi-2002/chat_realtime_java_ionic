<template>
    <div v-if="metadata"
        class="relative flex items-center w-full max-w-full border border-slate-300 dark:border-slate-600 rounded-md overflow-hidden bg-slate-100 dark:bg-gray-900 shadow-sm group mb-2 px-2">

        <div v-if="metadata.image" class="w-16 h-16 sm:w-24 sm:h-16 shrink-0 overflow-hidden rounded-lg my-2">
            <img :src="metadata.image" class="w-full h-full object-cover"
                @error="(e: any) => e.target.style.display = 'none'" />
        </div>

        <div class="p-2 flex-1 min-w-0">
            <p class="font-bold text-[13px] line-clamp-1 text-slate-800 dark:text-white leading-tight">
                {{ metadata.title || 'No title available' }}
            </p>
            <p v-if="metadata.description"
                class="text-[11px] text-slate-600 dark:text-gray-400 line-clamp-1 mt-0.5 italic">
                {{ metadata.description }}
            </p>
            <div class="flex items-center gap-1 mt-1 text-blue-500">
                <i class="fa-solid fa-link text-[8px]"></i>
                <span class="text-[10px] truncate font-bold">{{ hostname }}</span>
            </div>
        </div>

        <button @click="$emit('close')"
            class="absolute top-1 right-1 cursor-pointer bg-black/30 dark:bg-slate-700 text-white rounded-full w-6 h-6 flex items-center justify-center text-[10px] opacity-0 group-hover:opacity-100 transition">
            <i class="fa-solid fa-xmark"></i>
        </button>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { LinkMetadataType } from '@/types/common';
const props = defineProps<{ metadata: LinkMetadataType }>();
defineEmits(['close']);

const hostname = computed(() => {
    try { return new URL(props.metadata.url).hostname; }
    catch { return ''; }
});
</script>