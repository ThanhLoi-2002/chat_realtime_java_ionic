<template>
    <div>
        <!-- MEDIA -->
        <collapse v-model:isOpen="open.media" :title="`${t('image')}/${t('video')}`">
            <div class="grid grid-cols-4 gap-2">
                <img v-for="(i, index) in conversationStorage.images.slice(0, 8)" :key="index" :src="i.file.url"
                    class="aspect-square rounded bg-gray-200 dark:bg-slate-700 hover:scale-105 transition cursor-pointer" />
            </div>

            <ion-button class="btn mx-auto w-full normal-case" @click="goTo('storage/image')">
                {{ t("seeAll") }}
            </ion-button>
        </collapse>

        <!-- FILE -->
        <collapse v-model:isOpen="open.file" :title="t('File')">
            <div class="flex items-center gap-3 p-2 rounded
                                bg-gray-100 dark:bg-slate-800
                                hover:bg-gray-200 dark:hover:bg-slate-700
                                cursor-pointer transition" @click="openFile">
                <div class="w-10 h-10 bg-purple-500 rounded flex items-center justify-center text-white">
                    ▶
                </div>

                <div class="flex-1">
                    <div class="truncate dark:text-white">Record_2025.mp4</div>
                    <div class="text-xs text-gray-400">3.78 MB</div>
                </div>

                <div class="text-xs text-gray-400">26/12/2025</div>
            </div>

            <ion-button class="btn mx-auto w-full normal-case" @click="goTo('storage/file')">{{ t("seeAll")
                }}</ion-button>
        </collapse>

        <!-- LINK -->
        <section class="p-4 border-b border-gray-200 dark:border-slate-700">
            <div class="font-medium mb-2 dark:text-white">{{ t("link") }}</div>
            <div class="text-gray-400 text-sm">
                {{ t("noLinkShared") }}
            </div>
        </section>

        <!-- SECURITY -->
        <collapse v-model:isOpen="open.security" :title="t('setUpSecurity')">
            <div>
                <div class="dark:text-white">{{ t('messagesDeleteThemselves') }}</div>
                <div class="text-xs text-gray-400">{{ t('never') }}</div>
            </div>

            <!-- SWITCH -->
            <div class="flex justify-between items-center">
                <span class="dark:text-white">{{ t('hideChat') }}</span>

                <button @click="isHidden = !isHidden" class="w-10 h-6 flex items-center rounded-full p-1 transition"
                    :class="isHidden ? 'bg-blue-500' : 'bg-gray-300 dark:bg-slate-600'">
                    <div class="w-4 h-4 bg-white rounded-full shadow transition"
                        :class="isHidden ? 'translate-x-4' : ''" />
                </button>
            </div>

            <div class="text-red-500 hover:text-red-600 cursor-pointer transition" @click="deleteConversation">
                {{ t('deleteChatHistory') }}
            </div>
        </collapse>
    </div>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { reactive, ref } from 'vue';

const emit = defineEmits<{
    (e: 'update:currentView', value: string): void
}>()

const goTo = (view: string) => {
    emit('update:currentView', view)
}

const conversationStorage = useConversationStore()
const { t } = useTranslate()
const openFile = () => console.log('open file')
const deleteConversation = () => console.log('delete')

/* STATE */
const isHidden = ref(false)

const open = reactive({
    media: true,
    file: true,
    security: true
})

</script>