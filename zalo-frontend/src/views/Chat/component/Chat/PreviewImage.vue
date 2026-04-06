<template lang="">
    <div class="fixed inset-0 z-50 flex bg-black/90" @click.self="closePreview">
        <!-- CLOSE BUTTON -->
        <button @click="closePreview" class="absolute top-4 right-4 z-20 
           w-8 h-8 flex items-center justify-center 
           bg-white/10 hover:bg-white/20 
           rounded-full text-white text-lg cursor-pointer">
            <i class="fa fa-close" />
        </button>

        <!-- MAIN IMAGE -->
        <div class="relative flex-1 flex items-center justify-center" @click.stop>
            <img :src="messStorage.previewImage?.file?.url" class="max-w-[90%] max-h-[90%] object-contain transition duration-300" />
        </div>

        <!-- RIGHT SIDEBAR -->
        <div ref="scroll" class="hidden md:flex w-24 mt-16 bg-black/80 flex-col items-center py-4 gap-3 overflow-y-auto"
            @click.stop @scroll="handleScroll">
            <img v-for="(mess, i) in imageList" :key="i" :src="mess.file?.url" @click="handlePreviewImage(mess)"
                class="w-16 h-16 object-cover rounded cursor-pointer opacity-70 border border-white/20 transition"
                :class="mess.id === messStorage.previewImage?.id
                    ? 'border-2 border-red-400 opacity-100 scale-105'
                    : ''" />
        </div>

        <div :class="[style.text.primary, 'absolute left-2 bottom-4 flex items-center gap-2']">
            <CircleAvatar :user="messStorage.previewImage?.sender" />

            <div class="flex flex-col gap-1" :class="[style.text.primary]">
                <span>{{ messStorage.previewImage?.sender.username }}</span>
                <span class="text-xs">{{ formatSeparatorTime(messStorage.previewImage?.ct) }}</span>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import { useDateTime } from '@/composables/useDateTime';
import { useConversationStore } from '@/stores/conversation.storage';
import { useMessageStore } from '@/stores/message.storage';
import { MessageType } from '@/types/entities';
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { style } from '@/assets/tailwindcss';
import { useScroll } from '@/composables/useScroll';
import { MessageFilter } from '@/types/common';
import { MessageEnum } from '@/types/enum';

const scroll = ref<HTMLElement | null>(null)

const messStorage = useMessageStore()
const convStorage = useConversationStore()
const { formatSeparatorTime } = useDateTime()
const { scrollToBottom, onScroll } = useScroll()

const closePreview = () => {
    messStorage.setPreviewImage(undefined)
}

const handleKey = (e: KeyboardEvent) => {
    if (e.key === 'Escape') closePreview()
}

const handleScroll = () => {
    // Chỉ gọi load more khi gần đầu trang và còn dữ liệu
    if (messStorage.imagesHasMore && convStorage.conversation) {
        const options: MessageFilter = {
            conversationId: convStorage.conversation.id,
            limit: 20,
            lastId: messStorage.images.at(-1)?.id ?? undefined,
            contentType: MessageEnum.IMAGE
        }
        onScroll(scroll.value, () =>
            messStorage.getImageMessages(options)
        )
    }
}

const handlePreviewImage = (pi: MessageType) => {
    messStorage.setPreviewImage(pi)
}

const imageList = computed(() =>
    [...messStorage.images].reverse()
)

onMounted(() => {
    window.addEventListener('keydown', handleKey)
})
onBeforeUnmount(() => {
    window.removeEventListener('keydown', handleKey)
})

watch(() => scroll.value, () => {
    scrollToBottom(scroll.value, true)
})
</script>