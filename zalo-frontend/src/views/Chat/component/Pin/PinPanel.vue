<template>
    <div class="h-full flex flex-col overflow-auto">
        <!-- HEADER -->
        <div class="p-4 flex items-center gap-3 border-b border-slate-300 dark:border-slate-700"
            :class="[style.text.secondary]">
            <i class="fa fa-arrow-left cursor-pointer" @click="$emit('back')"></i>
            <span class="font-semibold m-auto">{{ t("pin") }}</span>
        </div>

        <!-- Card Item -->
        <div class="px-4 py-3 flex flex-col gap-4">
            <div v-for="pin in pinStorage.pins" :key="pin.id"
                class="border border-gray-300 dark:border-slate-700 rounded-lg p-4 shadow-md cursor-pointer">
                <!-- Header: Avatar & Title -->
                <div class="flex items-center gap-3 mb-3">
                    <CircleAvatar :user="pin.createdBy" :is-disabled="true" />
                    <div class="flex flex-col">
                        <span class="font-bold text-sm" :class="[style.text.primary]">{{ pin.message.sender.username
                            }}</span>
                        <div class="flex items-center gap-1 text-gray-400 text-xs">
                            <ion-icon :icon="chatbubbleOutline" class="text-blue-400" />
                            <span>{{ t('pinMessage') }}</span>
                        </div>
                    </div>
                </div>

                <!-- Body: Message Content -->
                <div class="text-gray-200 text-sm mb-3">
                    <div v-if="pin.message.contentType === MessageEnum.TEXT" :class="[style.text.secondary]"
                        class="line-clamp-3">
                        <p class="font-bold mb-1" :class="[style.text.secondary]">{{ pin.message.sender.username }}</p>
                        <p>{{ pin.message.content }}</p>
                    </div>

                    <div v-else-if="pin.message.contentType === MessageEnum.IMAGE" class="flex items-center gap-2">
                        <div class="w-12 h-12 rounded overflow-hidden border border-gray-600">
                            <img :src="pin.message?.attachments[0]?.secureUrl" class="object-cover w-full h-full" />
                        </div>
                        <div>
                            <p class="font-bold mb-1" :class="[style.text.secondary]">{{ pin.message.sender.username }}</p>
                            <span class="italic text-gray-500 text-xs">{{ pin.message.content }}</span>
                        </div>
                    </div>

                    <div v-else-if="pin.message.contentType === MessageEnum.FILE"
                        class="flex items-center gap-2 bg-gray-100 dark:bg-slate-800 p-2 rounded">
                        <div class="p-2 bg-white rounded shadow-sm">
                            <ion-icon :icon="documentOutline" class="text-xl text-blue-500" />
                        </div>
                        <div class="flex flex-col overflow-hidden">
                            <p class="font-bold mb-1" :class="[style.text.secondary]">{{ pin.message.sender.username }}</p>
                            <span class="truncate font-medium text-xs dark:text-gray-300">
                                {{ pin.message.attachments[0]?.name }}.{{ pin.message.attachments[0]?.secureUrl.split('.').pop() }}
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Footer: Time & Link -->
                <div class="flex items-center gap-1 text-xs text-gray-500 border-t border-gray-700/50 pt-2">
                    <span>{{ formatDateTime(pin.message.ct) }} |</span>
                    <span class="text-blue-400 cursor-pointer font-medium">{{ t('seeOriginalMessage') }}</span>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import { useDateTime } from '@/composables/useDateTime';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { usePinStore } from '@/stores/pin.storage';
import { MessageEnum } from '@/types/enum';
import { chatbubbleOutline, documentOutline } from 'ionicons/icons';
import { onMounted } from 'vue';

const { t } = useTranslate()
const pinStorage = usePinStore()
const convStorage = useConversationStore()
const { formatDateTime } = useDateTime()

onMounted(async () => {
    if (convStorage.conversation?.id)
        await pinStorage.getPins(convStorage.conversation.id)
})
</script>