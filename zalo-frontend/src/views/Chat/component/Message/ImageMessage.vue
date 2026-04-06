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
            <img v-else :src="message.file.url" @click="handlePreviewImage(message)"
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

    <Reactions :message="message" />
</template>
<script setup lang="ts">
import { useDateTime } from '@/composables/useDateTime';
import { useTranslate } from '@/composables/useTranslate';
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { style } from '@/assets/tailwindcss';
import { useMessageStore } from '@/stores/message.storage';
import Reactions from '../Reaction/Reactions.vue';
import { MessageType } from '@/types/entities';

const props = defineProps<{
    setBubbleRef?: (el: HTMLElement | null) => void
    message: any
    isOwner: boolean
}>()

const { formatTime } = useDateTime()
const messStorage = useMessageStore()
const { t } = useTranslate()

const el = ref<HTMLElement | null>(null)

const handlePreviewImage = (mess: MessageType) => {
    messStorage.setPreviewImage(mess)
}

onMounted(() => {
    props.setBubbleRef?.(el.value ?? null)
})
onBeforeUnmount(() => {
    props.setBubbleRef?.(null)
})
</script>