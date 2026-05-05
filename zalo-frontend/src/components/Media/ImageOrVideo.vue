<template>
    <div v-if="isVideo(media.secureUrl)" class="relative">
        <img :src="media.secureUrl.replace(/\.[^/.]+$/, '.jpg')" class="object-cover opacity-80" />
        <div class="absolute inset-0 flex items-center justify-center">
            <i class="fa-solid fa-circle-play text-white text-3xl shadow-lg"></i>
        </div>
    </div>

    <img v-else :src="media.secureUrl"
        class="aspect-square rounded bg-gray-200 dark:bg-slate-700 hover:scale-105 transition cursor-pointer" />
</template>
<script setup lang="ts">
import { useMedia } from '@/composables/useMedia';
import { MediaType } from '@/types/entities';


const props = defineProps<{
    media: MediaType
}>()

const { isVideo } = useMedia()
</script>