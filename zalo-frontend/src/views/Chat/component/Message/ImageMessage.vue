<template>
    <div ref="el" :class="[
        'text-sm flex flex-col relative min-w-12',
    ]">
        <!-- USERNAME -->
        <span v-if="message.isFirst && !isOwner" class="text-xs text-slate-400 dark:text-slate-300">
            {{ message.sender?.username }}
        </span>

        <!-- CONTENT -->
        <div :class="[message.showTime ? 'py-1' : 'py-0.5', 'max-w-62.5 md:max-w-100']">
            <div v-if="message.stt === -1"
                class="flex flex-col justify-center items-center w-20 h-20 md:w-30 md:h-30 bg-slate-300 dark:bg-slate-500 rounded-xl cursor-pointer hover:opacity-90 transition">
                <i class="fa-solid fa-image text-6xl md:text-8xl"></i>
                <span class="italic text-gray-700 dark:text-gray-800 text-[10px] md:text-sm">
                    {{ t('deleted') }}
                </span>
            </div>

            <div v-else-if="message.attachments?.length > 0" class="py-1 max-w-70 md:max-w-105">
                <div class="flex flex-wrap gap-1 rounded-xl overflow-hidden border border-black/5 shadow-sm">

                    <div v-for="(img, index) in message.attachments" :key="index"
                        class="relative group cursor-pointer overflow-hidden rounded" :class="[
                            // Cấu hình Flex Item:
                            // - 'flex-auto': Tự giãn (flex-grow) để chiếm hết width còn lại của hàng
                            // - 'h-25': Chiều cao cố định cho mỗi hàng ảnh
                            // - 'min-w-[30%]': Chiều rộng tối thiểu để ảnh không quá nhỏ (ít nhất 3 ảnh/hàng)
                            'flex-auto h-25 md:h-32.5 min-w-[30%] aspect-auto',

                            // Xử lý bo góc đặc biệt nếu chỉ có 1 ảnh
                            message.attachments.length === 1 ? 'rounded-xl h-auto aspect-auto' : ''
                        ]">

                        <img :src="img.secureUrl" @click="handlePreviewImage(message)"
                            class="w-full h-full object-cover group-hover:opacity-95 transition-opacity duration-300" />
                    </div>
                </div>
            </div>
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
import { MediaType } from '@/types/entities';

const props = defineProps<{
    setBubbleRef?: (el: HTMLElement | null) => void
    message: any
    isOwner: boolean
}>()

const { formatTime } = useDateTime()
const messStorage = useMessageStore()
const { t } = useTranslate()

const el = ref<HTMLElement | null>(null)

// Sửa lại hàm xử lý click
const handlePreviewImage = (mess: MediaType) => {
    // Truyền cả message và index của ảnh được chọn
    messStorage.setPreviewImage(mess,)
}

onMounted(() => {
    props.setBubbleRef?.(el.value ?? null)
})
onBeforeUnmount(() => {
    props.setBubbleRef?.(null)
})
</script>