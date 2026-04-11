<template>
    <div class="absolute flex gap-1 -bottom-3 right-1 cursor-pointer">
        <div @click="reactionRef?.present()" v-if="groupedReactions?.length > 0" class="flex items-center gap-1 px-1
               bg-white dark:bg-slate-700 border border-slate-300 dark:border-slate-600 
               rounded-full shadow text-xs">
            <!-- icons -->
            <div class="flex items-center -space-x-0.5">
                <span v-for="(r, index) in groupedReactions" :key="index" class="text-sm">
                    {{ EMOJI_MAP[r.type as keyof typeof EMOJI_MAP] }}
                </span>
            </div>

            <!-- count -->
            <span class="text-gray-700 dark:text-gray-200 font-medium">
                {{ totalCount }}
            </span>
        </div>

        <!-- Vùng like hiển thị khi hover: đặt ngoài bubble hoặc trên mép tùy ý -->
        <div class="flex items-center gap-2
                opacity-0 transition-opacity duration-150
                pointer-events-none group-hover:pointer-events-auto"
            :class="[message.stt != -1 && 'group-hover:opacity-100']">

            <div class="relative group/like">
                <button
                    class="w-4 h-4 md:w-5 md:h-5 flex items-center justify-center rounded-full bg-slate-100 dark:bg-gray-700 hover:bg-slate-200 dark:hover:bg-gray-600 shadow-sm p-1">
                    <i class="far fa-thumbs-up text-black dark:text-white text-[10px] md:text-xs"></i>
                </button>

                <!-- REACTIONS: ẩn mặc định, hiện khi hover vào nút like -->
                <div class="absolute bottom-full -mb-1 -translate-x-1/2 hidden group-hover/like:flex gap-1
                            bg-white dark:bg-gray-700 shadow-lg rounded-full px-2 py-1"
                    :class="[isOwner ? '-right-20' : 'left-1/2']">
                    <span class="cursor-pointer hover:scale-125 transition" v-for="(value, key) in EMOJI_MAP" :key="key"
                        @click="addReaction(key)">{{ value }}</span>
                    <span :class="[message.reactions.length > 0 && message.reactions.some(i => i.cu == userStorage.user?.id) ? '' : 'hidden', style.text.primary]" @click="deleteAllReaction">x</span>
                </div>
            </div>
        </div>
    </div>

    <Modal ref="reactionRef" :title="t('reaction')">
        <ReactionModal :reactions="message.reactions"/>
    </Modal>
</template>
<script setup lang="ts">
import { computed, ref } from 'vue'
import { MessageType } from '@/types/entities'
import { ReactionEnum } from '@/types/enum';
import { useMessageStore } from '@/stores/message.storage';
import { EMOJI_MAP } from '@/utils/constant';
import { useUserStore } from '@/stores/user.storage';
import Modal from '@/components/Modal/Modal.vue';
import { useTranslate } from '@/composables/useTranslate';
import ReactionModal from './ReactionModal.vue';
import { style } from '@/assets/tailwindcss';

const props = defineProps<{
    message: MessageType
}>()

const { t } = useTranslate()
const userStorage = useUserStore()
const messageStorage = useMessageStore()
const isOwner = props.message.sender.id === userStorage.user?.id
const reactionRef = ref()

// group theo type
const groupedReactions = computed(() => {
    const map: Record<string, number> = {}

    props.message.reactions?.forEach(r => {
        map[r.type] = (map[r.type] || 0) + 1
    })

    return Object.entries(map)
        .sort((a, b) => b[1] - a[1])  // sort theo count giảm dần
        .slice(0, 3)                    // chỉ lấy 3 icon
        .map(([type, count]) => ({
            type,
            count
        }))
})

// tổng số reaction
const totalCount = computed(() => {
    return props.message.reactions.reduce((acc, reaction) => {
        return acc + reaction.count
    }, 0)
})

const addReaction = (key: keyof typeof ReactionEnum) => {
    messageStorage.addReaction(props.message.id, props.message.conversationId, key)
}

const deleteAllReaction = () => {
    messageStorage.deleteAllReaction(props.message.id, props.message.conversationId)
}

</script>