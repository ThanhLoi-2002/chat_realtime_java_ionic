<template lang="">
    <div ref="el" :class="[
                'text-sm flex flex-col relative min-w-12',
            ]">
                <!-- USERNAME -->
                <span v-if="message.isFirst && !isOwner" class="text-xs text-slate-400 dark:text-slate-300">
                    {{ message.sender?.username }}
                </span>

                <!-- CONTENT -->
                <div :class="[message.showTime ? 'py-1' : 'py-0.5']">
                    <!-- nếu bị thu hồi -->
                     <div v-if="message.stt === -1" class="flex flex-col justify-center items-center w-20 h-20 md:w-30 md:h-30 bg-slate-300 dark:bg-slate-500 rounded-xl cursor-pointer hover:opacity-90 transition">
                        <i class="fa-solid fa-image text-6xl md:text-8xl"></i>
                        <span class="italic text-gray-700 dark:text-gray-800 text-[10px] md:text-sm">
                        {{ t('deleted') }}
                    </span>
                     </div>
                    

                    <!-- nếu là image -->
                    <img v-else :src="message.file.url"
                        @click="handlePreviewImage(message.file.url)"
                        class="max-w-20 md:max-w-30 rounded-xl object-cover cursor-pointer hover:opacity-90 transition" />
                </div>

                <!-- TIME -->
                <span v-if="message.showTime" :class="[
                    'text-[10px] md:text-xs',
                    'text-slate-300 dark:text-slate-400'
                ]">
                    {{ formatTime(message.ct) }}
                </span>
            </div>

            <div v-if="previewImage" class="fixed inset-0 z-50 flex items-center justify-center 
           bg-black/80 backdrop-blur-sm">

            <!-- overlay click để đóng -->
            <div class="absolute inset-0" @click="closePreview"></div>

            <!-- ảnh -->
            <img :src="previewImage"
                class="relative max-w-[90vw] max-h-[90vh] min-w-[60vw] min-h-[60vh] rounded-xl shadow-2xl z-10 object-contain" />

            <!-- nút close -->
            <button @click="closePreview" class="absolute top-5 right-5 z-20 
               w-6 h-6 md:w-10 md:h-10 flex items-center justify-center 
               bg-white/10 hover:bg-white/20 
               rounded-full text-white text-xs md:text-lg">
                ✕
            </button>
        </div>
</template>
<script setup lang="ts">
import { useDateTime } from '@/composables/useDateTime';
import { useTranslate } from '@/composables/useTranslate';
import { onBeforeUnmount, onMounted, ref } from 'vue';

const props = defineProps<{
    setBubbleRef?: (el: HTMLElement | null) => void
    message: any
    isOwner: boolean
}>()

const { formatTime } = useDateTime()
const { t } = useTranslate()

const el = ref<HTMLElement | null>(null)

const previewImage = ref<string | null>(null)

const handlePreviewImage = (url: string) => {
    previewImage.value = url
}

const closePreview = () => {
    previewImage.value = null
}

onMounted(() => {
    props.setBubbleRef?.(el.value ?? null)
})
onBeforeUnmount(() => {
    props.setBubbleRef?.(null)
})
</script>