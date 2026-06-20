<template>
    <div v-if="pinStorage.pinList.length != 0" ref="pinnedContainer" class="
        px-4 py-2 flex justify-between items-center bg-gray-100 dark:bg-gray-800 border hover:bg-slate-200 dark:hover:bg-slate-700
        border-slate-300 dark:border-slate-800
        z-10 rounded-sm shadow min-w-[98%]
        text-xs md:text-base" :class="[style.text.primary]">

        <div v-if="!isExpanded" class="flex items-center gap-2.5 cursor-pointer w-full" :class="style.text.primary">
            <i class="text-blue-400 text-xl block fas fa-comment-dots"></i>
            <div class="flex-1 flex flex-col min-w-0" @click="jumpToMessage(pinStorage.pinList[0]?.messageId)">
                <span class="text-xs" :class="style.text.primary">{{ t('message') }}</span>
                <span class="flex items-center gap-1 text-xs truncate font-medium" :class="[style.text.primary]">
                    {{ pinStorage.pinList[0]?.message.sender.username }}:

                    <template v-if="pinStorage.pinList[0]?.message.stt == -1">
                        <p :class="[style.text.muted, 'italic']">{{ t('messageHasBeenWithdrawn') }}</p>
                    </template>

                    <template v-else-if="pinStorage.pinList[0]?.message.contentType === MessageEnum.TEXT">
                        {{ pinStorage.pinList[0]?.message.content }}
                    </template>
                    <template v-else-if="pinStorage.pinList[0]?.message.contentType === MessageEnum.IMAGE">
                        <i class="fas fa-image mr-1 opacity-70"></i>
                        <span>{{ pinStorage.pinList[0]?.message.content }}</span>
                    </template>
                    <template v-else-if="pinStorage.pinList[0]?.message.contentType === MessageEnum.FILE">
                        <i class="fas fa-file-alt mr-1 opacity-70"></i>
                        {{ pinStorage.pinList[0]?.message.attachments[0]?.name }}.{{
                            pinStorage.pinList[0]?.message.attachments[0]?.secureUrl.split('.').pop() }}
                    </template>
                </span>
            </div>

            <div v-if="pinStorage.pinList.length > 1" @click.stop="isExpanded = true"
                class="ml-2 flex items-center px-2 py-1 rounded border border-slate-400 bg-gray-200 hover:bg-gray-300 dark:bg-gray-800/50 dark:hover:bg-gray-700 transition-all">
                <span class="text-xs font-bold mr-1" :class="[style.text.primary]">+{{ pinStorage.pinList.length - 1 }}
                    {{ t('pin') }}</span>
                <i class="text-[10px] fas fa-angle-double-down" :class="[style.text.primary]"></i>
            </div>

            <div class="relative ml-2">
                <i @click.stop="toggleMenu(pinStorage.pinList[0].id)"
                    class="fas fa-ellipsis-h rounded border border-slate-400 bg-gray-200 hover:bg-gray-300 dark:bg-gray-800/50 dark:hover:bg-gray-700 p-1 cursor-pointer"
                    :class="[style.text.primary]"></i>

                <div v-if="activeMenuId === pinStorage.pinList[0].id"
                    class="absolute right-0 top-full mt-2 w-32 bg-white dark:bg-slate-700 shadow-xl rounded border border-gray-200 dark:border-slate-600 z-2 py-1">
                    <button @click.stop="handleUnpin(pinStorage.pinList[0].id)"
                        class="w-full text-left px-3 py-2 text-xs text-red-500 hover:bg-gray-100 dark:hover:bg-slate-600 flex items-center gap-2">
                        <i class="fas fa-thumbtack-slash"></i> {{ t('unPinMessage') }}
                    </button>
                </div>
            </div>
        </div>

        <transition name="slide-down">
            <div v-if="isExpanded" class="absolute top-0 left-0 w-full bg-slate-100 dark:bg-gray-800 shadow-2xl z-10">
                <div class="flex items-center justify-between px-4 py-2 border-b border-gray-500/50">
                    <span class="font-semibold text-sm" :class="[style.text.secondary]">{{ t('pinList') }} ({{
                        pinStorage.pinList.length
                        }})</span>
                    <button @click="isExpanded = false" class="flex items-center text-xs text-gray-400 cursor-pointer">
                        {{ t('compact') }}
                        <i class="fas fa-angle-double-up ml-1"></i>
                    </button>
                </div>

                <div class="max-h-75 overflow-y-auto divide-y divide-gray-500/50">
                    <div v-for="pin in pinStorage.pinList" :key="pin.id" @click="jumpToMessage(pin.messageId)"
                        class="flex gap-2.5 items-center px-4 py-3 hover:bg-slate-200 dark:hover:bg-slate-700/50 transition-colors group cursor-pointer">
                        <i class="text-blue-400 text-xl block fas fa-comment-dots"></i>
                        <div class="flex-1 flex flex-col min-w-0">
                            <span class="text-xs" :class="style.text.primary">{{ t('message') }}</span>
                            <span class="text-xs truncate font-medium" :class="[style.text.primary]">
                                {{ pin.message.sender.username }}:

                                <template v-if="pin.message.stt == -1">
                                    <span :class="[style.text.muted, 'italic']">{{ t('messageHasBeenWithdrawn') }}</span>
                                </template>

                                <template v-else-if="pin.message.contentType === MessageEnum.TEXT">
                                    {{ pin.message.content }}
                                </template>
                                <template v-else-if="pin.message.contentType === MessageEnum.IMAGE">
                                    <i class="fas fa-image mr-1 opacity-70"></i>
                                    <span>{{ pin.message.content }}</span>
                                </template>
                                <template v-else-if="pin.message.contentType === MessageEnum.FILE">
                                    <i class="fas fa-file-alt mr-1 opacity-70"></i> {{
                                        pin.message.attachments[0]?.name }}.{{
                                        pin.message.attachments[0]?.secureUrl.split('.').pop() }}
                                </template>
                            </span>
                        </div>

                        <div class="relative">
                            <i @click.stop="toggleMenu(pin.id)"
                                class="fas fa-ellipsis-h rounded border-slate-400 hover:bg-gray-300 dark:hover:bg-gray-700 p-1 cursor-pointer"
                                :class="[style.text.primary]"></i>

                            <div v-if="activeMenuId === pin.id"
                                class="absolute right-0 top-full mt-1 w-32 bg-white dark:bg-slate-700 shadow-xl rounded border border-gray-200 dark:border-slate-600 z-50 py-1">
                                <button @click.stop="handleUnpin(pin.id)"
                                    class="w-full text-left px-3 py-2 text-xs text-red-500 hover:bg-gray-100 dark:hover:bg-slate-600 flex items-center gap-2">
                                    <i class="fas fa-thumbtack-slash"></i> {{ t('unPinMessage') }}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <button
                    class="w-full py-2 text-sm cursor-pointer text-gray-400 hover:bg-white/5 flex items-center justify-center border-t border-gray-500/50"
                    :class="[style.text.secondary]" @click="openPins">
                    {{ t('seeAllInCommunityBulletinBoard') }}
                    <i class="fas fa-chevron-right ml-1"></i>
                </button>
            </div>
        </transition>
    </div>
</template>

<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import { useMessage } from '@/composables/useMessage';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/App/conversation.storage';
import { usePinStore } from '@/stores/App/pin.storage';
import { useSystemStore } from '@/stores/system.storage';
import { MessageEnum } from '@/types/enum';
import { onClickOutside } from '@vueuse/core';
import { ref, watch } from 'vue';

const isExpanded = ref(false);
const activeMenuId = ref<number | null>(null); // Quản lý ID của menu đang mở
const { t } = useTranslate()
const pinnedContainer = ref<HTMLElement | null>(null);
const pinStorage = usePinStore()
const convStorage = useConversationStore()
const systemStorage = useSystemStore()
const {jumpToMessage} = useMessage()

const emit = defineEmits(['update:isShowInfoSection'])

// --- Logics ---
const toggleMenu = (id: number) => {
    activeMenuId.value = activeMenuId.value === id ? null : id;
};

const handleUnpin = async (pinId: number) => {
    // Gọi hàm xóa ghim từ store của bạn
    activeMenuId.value = null;
    await pinStorage.removePinMessFromList(pinId, convStorage.conversation!.id)
};

const openPins = () => {
    emit('update:isShowInfoSection', true)
    systemStorage.setIsOpenPins(true)
}

// Click ra ngoài container ghim thì đóng cả danh sách mở rộng và menu nhỏ
onClickOutside(pinnedContainer, () => {
    isExpanded.value = false;
    activeMenuId.value = null;
});

// Đảm bảo đóng menu nhỏ khi đóng/mở danh sách lớn
watch(isExpanded, () => activeMenuId.value = null);

watch(() => convStorage.conversation?.id, async () => {
    if (convStorage.conversation?.id) {
        await pinStorage.getMessPinList(convStorage.conversation.id)
    }
}, { immediate: true });
</script>