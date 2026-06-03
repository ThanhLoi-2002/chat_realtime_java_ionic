<template>
    <div ref="el" :class="[
        'text-sm flex flex-col relative min-w-12 px-2 md:px-3 rounded-lg',
        'px-2 py-2',

        // Logic cho Background và Text
        // isOwner ? 'bg-blue-400 dark:bg-blue-900 text-white' : 'bg-white dark:bg-gray-800 dark:text-slate-100',

        // Logic Padding dựa trên reaction
        message.reactions?.length > 0 ? 'pt-2 pb-4' : 'py-0.5 md:py-2',
    ]" :data-id="message.id">
        <!-- USERNAME -->
        <span v-if="message.isFirst && !isOwner" class="text-xs text-slate-400 dark:text-slate-300">
            {{ message.sender?.username }}
        </span>

        <!-- CONTENT -->
        <div :class="[message.showTime ? 'py-1' : 'py-0.5']">
            <span v-if="message.stt === -1" class="italic text-gray-700 dark:text-gray-400">
                {{ isOwner ? t("youRecalledmessage") : t("messageHasBeenWithdrawn") }}
            </span>

            <ZaloSticker v-else :size="130" :sticker-item="message.sticker" :isHover="false" />

        </div>

        <!-- TIME -->
        <span v-if="message.showTime" :class="['text-[10px] md:text-xs text-slate-300 dark:text-slate-400']">
            {{ formatTime(message.ct) }}
        </span>
    </div>
</template>
<script setup lang="ts">
import { useDateTime } from "@/composables/useDateTime";
import { useTranslate } from "@/composables/useTranslate";
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useConversationStore } from "@/stores/conversation.storage";
import { useUserStore } from "@/stores/user.storage";
import ZaloSticker from "@/components/Sticker/Zalo/ZaloSticker.vue";
import { MessageType } from "@/types/entities";

type Message = MessageType & {
    isFirst: boolean
    showTime: boolean
}

const props = defineProps<{
    setBubbleRef?: (el: HTMLElement | null) => void;
    message: Message;
    isOwner: boolean;
}>();

const { formatTime } = useDateTime();
const { t } = useTranslate();
const convStorage = useConversationStore()
const userStorage = useUserStore()
const emit = defineEmits(['visible']);
let observer: IntersectionObserver | null = null;

const el = ref<HTMLElement | null>(null);

onMounted(() => {
    // Cấu hình: Khi tin nhắn hiện ra 50% diện tích thì tính là "đã xem"
    observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                emit('visible', props.message.id);
            }
        });
    }, { threshold: 0.5 });

    if (el.value) observer.observe(el.value);

    props.setBubbleRef?.(el.value ?? null);
});
onBeforeUnmount(() => {
    observer?.disconnect()
    props.setBubbleRef?.(null);
});
</script>