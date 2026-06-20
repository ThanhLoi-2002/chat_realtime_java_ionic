<!-- component/Chat/SelectionToolbar.vue -->
<template>
    <div v-if="actionStore.isSelectionMode" class="relative bottom-0 left-0 w-full bg-white dark:bg-slate-800 border-t 
                border-gray-200 dark:border-slate-700 z-40 shadow-lg px-4 py-3 
                flex items-center justify-between animate-slide-up">

        <div class="flex items-center gap-3">
            <span class="bg-blue-600 text-white text-xs font-bold px-2 py-1 rounded">
                {{ actionStore.shareMessages.length }}
            </span>
            <span class="text-sm font-medium dark:text-white">{{ t('selected') }}</span>
        </div>

        <div class="flex items-center gap-6">
            <!-- Copy -->
            <button @click="copyAll"
                class="flex flex-col items-center gap-1 text-gray-600 dark:text-gray-300 hover:text-blue-500 cursor-pointer">
                <i class="fas fa-copy"></i>
                <span class="text-[10px]">{{ t('copy') }}</span>
            </button>

            <!-- Forward/Share -->
            <button @click="shareAll"
                class="flex flex-col items-center gap-1 text-gray-600 dark:text-gray-300 hover:text-blue-500 cursor-pointer">
                <i class="fas fa-share"></i>
                <span class="text-[10px]">{{ t('share') }}</span>
            </button>

            <!-- Delete -->
            <button @click="deleteAll"
                class="flex flex-col items-center gap-1 text-red-500 hover:text-red-600 cursor-pointer">
                <i class="fas fa-trash"></i>
                <span class="text-[10px]">{{ t('delete') }}</span>
            </button>

            <div class="h-8 w-px bg-gray-200 dark:bg-gray-700"></div>

            <!-- Cancel -->
            <button @click="actionStore.cancelSelectionMode"
                class="text-sm font-bold text-blue-600 hover:text-blue-700 cursor-pointer">
                {{ t('cancel') }}
            </button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useChatActionStore } from '@/composables/useChatAction';
import { useTranslate } from '@/composables/useTranslate';
const actionStore = useChatActionStore();
const { t } = useTranslate()

const copyAll = () => { /* Logic copy các nội dung tin nhắn */ }
const shareAll = () => { actionStore.openShare() }
const deleteAll = () => { /* Logic xóa hàng loạt */ }
</script>

<style scoped>
.animate-slide-up {
    animation: slideUp 0.2s ease-out;
}

@keyframes slideUp {
    from {
        transform: translateY(100%);
    }

    to {
        transform: translateY(0);
    }
}
</style>