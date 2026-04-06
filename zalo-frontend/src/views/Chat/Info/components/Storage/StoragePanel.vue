<template>
    <div class="h-full overflow-hidden flex flex-col">

        <!-- HEADER -->
        <div class="p-4 flex justify-between items-center gap-3 border-b dark:border-slate-700"
            :class="[style.text.secondary]">

            <i class="fa fa-arrow-left cursor-pointer" @click="$emit('back')"></i>
            <span class="font-semibold m-auto">{{ t("storage") }}</span>

            <span class="text-blue-400 cursor-pointer">{{ t("select") }}</span>
        </div>

        <!-- TABS -->
        <div class="flex border-b border-slate-700">
            <div v-for="item in tabs" :key="item.key" @click="handleChangeTab(item.key)"
                class="flex-1 text-center py-2 cursor-pointer transition" :class="[
                    tab === item.key
                        ? 'text-blue-400 border-b-2 border-blue-400'
                        : 'text-gray-400'
                ]">
                {{ t(item.label) }}
            </div>
        </div>

        <transition name="slide">
            <template v-if="tab == tabs[0].key">
                <ImageVideo />
            </template>

            <template v-else-if="tab == tabs[1].key">
                <File />
            </template>

            <template v-else-if="tab == tabs[2].key">
                <Link />
            </template>
        </transition>
    </div>
</template>

<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import { useDateTime } from '@/composables/useDateTime';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { computed, ref } from 'vue';
import ImageVideo from './ImageVideo.vue';
import File from './File.vue';
import Link from './Link.vue';

const props = defineProps<{
    type: string
}>()

const emit = defineEmits(['back'])
const { t } = useTranslate()

const tabs = [
    { key: 'image', label: 'image/video' },
    { key: 'file', label: 'files' },
    { key: 'link', label: 'links' }
]

const activeTab = computed(() => {
    return props.type.replace('storage/', '')
})

const tab = ref(activeTab.value)

const handleChangeTab = (key: string) => {
    tab.value = key
}
</script>