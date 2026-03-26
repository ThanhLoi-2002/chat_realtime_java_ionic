<template lang="">
    <div ref="el" :class="[
                'text-sm flex flex-col relative min-w-12',
                    'px-2 py-0.5 md:px-4 md:py-2 border rounded-lg',
                    isOwner
                        ? 'bg-blue-400 text-white border-blue-500'
                        : 'bg-white dark:bg-gray-800 dark:text-slate-100 border-slate-300 dark:border-gray-700'
                
            ]">
                <!-- USERNAME -->
                <span v-if="message.isFirst && !isOwner" class="text-xs text-slate-400 dark:text-slate-300">
                    {{ message.sender?.username }}
                </span>

                <!-- CONTENT -->
                <div :class="[message.showTime ? 'py-1' : 'py-0.5']">
                    <!-- nếu bị thu hồi -->
                    <span v-if="message.stt === -1" class="italic text-gray-700 dark:text-gray-600">
                        {{ isOwner ? t('youRecalledmessage') : t('messageHasBeenWithdrawn') }}
                    </span>

                    <!-- text bình thường -->
                    <span v-else>
                        {{ message.content }}
                    </span>
                </div>

                <!-- TIME -->
                <span v-if="message.showTime" :class="[
                    'text-[10px] md:text-xs',
                    'text-slate-300 dark:text-slate-400'
                ]">
                    {{ formatTime(message.ct) }}
                </span>
            </div>
</template>
<script setup lang="ts">
import { useDateTime } from '@/composables/useDateTime';
import { useTranslate } from '@/composables/useTranslate';
import { ref, onMounted, onBeforeUnmount } from 'vue';

const props = defineProps<{
    setBubbleRef?: (el: HTMLElement | null) => void
    message: any
    isOwner: boolean
}>()

const { formatTime } = useDateTime()
const { t } = useTranslate()

const el = ref<HTMLElement | null>(null)

onMounted(() => {
    props.setBubbleRef?.(el.value ?? null)
})
onBeforeUnmount(() => {
    props.setBubbleRef?.(null)
})

</script>