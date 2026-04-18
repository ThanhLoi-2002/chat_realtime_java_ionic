<template>
    <div>
        <!-- MEDIA -->
        <collapse v-model:isOpen="open.media" :title="`${t('image')}/${t('video')}`">
            <div class="grid grid-cols-4 gap-2">
                <div v-for="(i, index) in messStorage.images.slice(0, 8)" :key="index"
                    class="aspect-square rounded bg-gray-200 dark:bg-slate-700 hover:scale-105 transition cursor-pointer"
                    @click="handlePreviewImage(i)">

                    <image-or-video :media="i" />
                </div>
            </div>

            <ion-button class="btn mx-auto w-full normal-case" @click="goTo('storage/image')">
                {{ t("seeAll") }}
            </ion-button>
        </collapse>

        <!-- FILE -->
        <collapse v-model:isOpen="open.file" :title="t('File')" customClass="gap-0!">
            <file-container v-for="(media, index) in messStorage.files.slice(0, 4)" :key="index" :media="media" />

            <ion-button class="btn mt-2 mx-auto w-full normal-case" @click="goTo('storage/file')">{{ t("seeAll")
                }}</ion-button>
        </collapse>

        <!-- LINK -->
        <section class="p-4 border-b border-gray-200 dark:border-slate-700">
            <div class="font-medium mb-2 dark:text-white">{{ t("link") }}</div>
            <link-container v-for="(link, index) in messStorage.links.slice(0, 4)" :key="index" :message="link" />

            <ion-button class="btn mt-2 mx-auto w-full normal-case" @click="goTo('storage/link')">{{ t("seeAll")
                }}</ion-button>
        </section>


    </div>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { useMessageStore } from '@/stores/message.storage';
import { MessageFilter } from '@/types/common';
import { MediaType } from '@/types/entities';
import { MessageEnum } from '@/types/enum';
import { onMounted, reactive, watch } from 'vue';
import FileContainer from './FileContainer.vue';
import Collapse from '@/components/collapse/Collapse.vue';
import LinkContainer from './LinkContainer.vue';
import ImageOrVideo from '@/components/Media/ImageOrVideo.vue';
import { appLimit } from '@/utils/constant';

const emit = defineEmits<{
    (e: 'update:currentView', value: string): void
}>()

const goTo = (view: string) => {
    emit('update:currentView', view)
}

const convStorage = useConversationStore()
const messStorage = useMessageStore()
const { t } = useTranslate()

const open = reactive({
    media: true,
    file: true,
})

const handlePreviewImage = (pi: MediaType) => {
    messStorage.setPreviewImage(pi)
}

const fetchImages = () => {
    const options: MessageFilter = {
        conversationId: convStorage.conversation!.id,
        limit: appLimit.imageVideosFirst,
        lastId: messStorage.images.at(-1)?.moduleId ?? undefined,
        contentType: MessageEnum.IMAGE
    }
    messStorage.getImageMessages(options)
}

const fetchFiles = () => {
    const options: MessageFilter = {
        conversationId: convStorage.conversation!.id,
        limit: appLimit.filesFirst,
        lastId: messStorage.files.at(-1)?.moduleId ?? undefined,
        contentType: MessageEnum.FILE
    }
    messStorage.getFileMessages(options)
}

const fetchLinks = () => {
    const options: MessageFilter = {
        conversationId: convStorage.conversation!.id,
        limit: appLimit.linksFirst,
        lastId: messStorage.links.at(-1)?.id ?? undefined,
        contentType: MessageEnum.TEXT,
        linkMetadata: true
    }
    messStorage.getLinkMessages(options)
}

onMounted(() => {
    fetchImages()
    fetchFiles()
    fetchLinks()
})

watch(() => convStorage.conversation?.id, () => {
    fetchImages()
    fetchFiles()
    fetchLinks()
})
</script>