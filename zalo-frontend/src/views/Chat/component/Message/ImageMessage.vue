<template>
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
            <div v-if="message.stt === -1"
                class="flex flex-col justify-center items-center w-20 h-20 md:w-30 md:h-30 bg-slate-300 dark:bg-slate-500 rounded-xl cursor-pointer hover:opacity-90 transition">
                <i class="fa-solid fa-image text-6xl md:text-8xl"></i>
                <span class="italic text-gray-700 dark:text-gray-800 text-[10px] md:text-sm">
                    {{ t('deleted') }}
                </span>
            </div>


            <!-- nếu là image -->
            <img v-else :src="message.file.url" @click="handlePreviewImage(message.file.url)"
                class="max-w-20 md:max-w-40 rounded-xl object-cover cursor-pointer hover:opacity-90 transition" />
        </div>

        <!-- TIME -->
        <span v-if="message.showTime" :class="[
            'text-[10px] md:text-xs',
            style.text.secondary
        ]">
            {{ formatTime(message.ct) }}
        </span>
    </div>

    <div v-if="previewImage" class="fixed inset-0 z-50 flex bg-black/90" @click.self="closePreview">

        <!-- MAIN IMAGE -->
        <div class="relative flex-1 flex items-center justify-center" @click.stop>
            <img :src="previewImage" class="max-w-[90%] max-h-[90%] object-contain transition duration-300" />
        </div>

        <!-- RIGHT SIDEBAR -->
        <div class="hidden md:flex w-24 mt-12 bg-black/80 flex-col items-center py-4 gap-3 overflow-y-auto" @click.stop>
            <img v-for="(img, i) in imageList" :key="i" :src="img" @click="previewImage = img"
                class="w-16 h-16 object-cover rounded cursor-pointer opacity-70 border border-white/20 transition"
                :class="img === previewImage
                    ? 'border-2 border-red-400 opacity-100 scale-105'
                    : ''" />
        </div>

        <!-- CLOSE BUTTON -->
        <button @click="closePreview" class="absolute top-4 right-4 z-20 
           w-10 h-10 flex items-center justify-center 
           bg-white/10 hover:bg-white/20 
           rounded-full text-white text-lg cursor-pointer">
            <i class="fa fa-close" />
        </button>

    </div>
</template>
<script setup lang="ts">
import { useDateTime } from '@/composables/useDateTime';
import { useTranslate } from '@/composables/useTranslate';
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { style } from '@/assets/tailwindcss';
import { useMessageStore } from '@/stores/message.storage';
import { MessageEnum } from '@/types/enum';

const props = defineProps<{
    setBubbleRef?: (el: HTMLElement | null) => void
    message: any
    isOwner: boolean
}>()

const { formatTime } = useDateTime()
const messStorage = useMessageStore()
const { t } = useTranslate()

const el = ref<HTMLElement | null>(null)

const previewImage = ref<string | null>(null)

const imageList = computed(() =>
    messStorage.messages
        .filter(m => m.contentType === MessageEnum.IMAGE && m.stt == 1)
        .map(m => m.file.url)
)

const handlePreviewImage = (url: string) => {
    previewImage.value = url
}

const closePreview = () => {
    previewImage.value = null
}

onMounted(() => {
    props.setBubbleRef?.(el.value ?? null)
    window.addEventListener('keydown', handleKey)
})
onBeforeUnmount(() => {
    props.setBubbleRef?.(null)
    window.removeEventListener('keydown', handleKey)
})

const handleKey = (e: KeyboardEvent) => {
    if (e.key === 'Escape') closePreview()
}
</script>