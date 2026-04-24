<script setup lang="ts">
import { ref } from 'vue';
import { IonIcon } from '@ionic/vue';
import { useTranslate } from '@/composables/useTranslate';
import { chevronBackOutline, chevronForwardOutline } from 'ionicons/icons';
import { useClassificationCardStore } from '@/stores/classificationCard.storage';

interface Tag {
    id: number;
    name: string;
}

const classCardStorage = useClassificationCardStore()
const { t } = useTranslate()
const activeTagId = ref(0);

const selectTag = (id: number) => {
    activeTagId.value = id;
};

const scrollContainer = ref<HTMLElement | null>(null);

const scroll = (direction: 'left' | 'right') => {
    if (scrollContainer.value) {
        const scrollAmount = 200; // Khoảng cách mỗi lần click
        scrollContainer.value.scrollBy({
            left: direction === 'left' ? -scrollAmount : scrollAmount,
            behavior: 'smooth'
        });
    }
};
</script>

<template>
    <div class="relative flex items-center w-full group py-2 bg-white dark:bg-transparent">

        <div
            class="absolute -left-3.5 z-10 h-full flex items-center pr-8 pointer-events-none opacity-0 group-hover:opacity-100 transition-opacity">
            <button @click="scroll('left')"
                class="pointer-events-auto w-8 h-8 rounded-full bg-white dark:bg-[#2a2f36] border border-slate-200 dark:border-slate-700 flex items-center justify-center shadow-md hover:scale-110 active:scale-95 transition-all">
                <ion-icon :icon="chevronBackOutline" class="text-slate-600 dark:text-slate-200 text-xs" />
            </button>
        </div>

        <div ref="scrollContainer" class="flex space-x-2 overflow-x-auto no-scrollbar scroll-smooth px-2">

            <button @click="selectTag(0)"
                class="px-4 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition-all duration-200 border"
                :class="[
                    activeTagId === 0
                        ? 'bg-blue-600 border-blue-600 text-white shadow-lg shadow-blue-500/30'
                        : 'bg-slate-100 dark:bg-[#2a2f36] border-transparent text-slate-600 dark:text-slate-300 hover:border-slate-300 dark:hover:bg-[#363c44]'
                ]">
                {{ t('all') }}
            </button>

            <button v-for="tag in classCardStorage.cards" :key="tag.id" @click="selectTag(tag.id)"
                class="px-4 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition-all duration-200 border"
                :class="[
                    activeTagId === tag.id
                        ? 'bg-blue-600 border-blue-600 text-white shadow-lg shadow-blue-500/30'
                        : 'bg-slate-100 dark:bg-[#2a2f36] border-transparent text-slate-600 dark:text-slate-300 hover:border-slate-300 dark:hover:bg-[#363c44]'
                ]">
                {{ tag.name }}
            </button>
        </div>

        <div
            class="absolute -right-3.5 z-10 h-full flex items-center pl-8 pointer-events-none opacity-0 group-hover:opacity-100 transition-opacity">
            <button @click="scroll('right')"
                class="pointer-events-auto w-8 h-8 rounded-full bg-white dark:bg-[#2a2f36] border border-slate-200 dark:border-slate-700 flex items-center justify-center shadow-md hover:scale-110 active:scale-95 transition-all">
                <ion-icon :icon="chevronForwardOutline" class="text-slate-600 dark:text-slate-200 text-xs" />
            </button>
        </div>
    </div>
</template>

<style>
.no-scrollbar::-webkit-scrollbar {
    display: none;
}

.no-scrollbar {
    -ms-overflow-style: none;
    scrollbar-width: none;
}

/* Hiệu ứng mượt cho các nút */
button {
    -webkit-tap-highlight-color: transparent;
}
</style>