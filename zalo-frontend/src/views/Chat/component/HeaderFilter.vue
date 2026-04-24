<template>
    <div
        class="flex items-center py-3 mx-4 gap-4 text-sm font-medium border-b border-gray-400 dark:border-gray-800 dark:bg-slate-800">
        <span
            class="text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400 pb-1 cursor-pointer">{{
                t("all") }}</span>
        <span class="text-gray-600 hover:text-gray-400 dark:text-gray-400 dark:hover:text-gray-200 cursor-pointer">{{
            t("haveNotReadYet") }}</span>

        <div id="tag-menu-trigger"
            class="flex items-center gap-1 ml-auto cursor-pointer text-gray-500 hover:text-slate-400 dark:text-gray-300 dark:hover:text-white">
            <span>{{ t("classify") }}</span>
            <ion-icon :icon="chevronDownOutline" class="text-xs"></ion-icon>
        </div>

        <ion-icon :icon="ellipsisHorizontalOutline"
            class="text-gray-500 hover:text-slate-400 dark:text-gray-400 cursor-pointer dark:hover:text-white text-lg"></ion-icon>

        <ion-popover ref="popoverRef" trigger="tag-menu-trigger" trigger-action="click" :dismiss-on-select="false"
            class="custom-popover">
            <div class="dark:text-gray-200 w-56 py-2 shadow-xl border border-gray-500 dark:border-gray-700 rounded-lg"
                :class="[style.bg.primary]">
                <div v-for="tag in classificationCardStorage.cards" :key="tag.id" @click="filterByTag(tag.id)"
                    class="flex items-center px-4 py-2.5 hover:bg-gray-300/30 dark:hover:bg-gray-700 cursor-pointer transition-colors">

                    <input type="checkbox" :value="tag.id" v-model="selectedTagIds" @click.stop class="mr-3 pointer-events-none rounded border-gray-300 text-blue-600 focus:ring-blue-500 
                        dark:border-gray-600 dark:bg-gray-700 dark:checked:bg-blue-500" />

                    <i class="fas fa-tag w-3 h-3 rounded-sm mr-3" :style="{ color: tag.color }"></i>
                    <span class="text-[14px]">{{ tag.name }}</span>
                </div>

                <div class="h-px bg-gray-400 dark:bg-gray-700 m-2"></div>

                <div @click="openManager"
                    class="px-4 py-2 dark:text-slate-200 hover:bg-gray-300/30 dark:hover:bg-gray-700 cursor-pointer text-[14px] text-nowrap">
                    {{ t("classificationTagManagement") }}
                </div>
            </div>
        </ion-popover>

        <modal ref="classificationTagRef" :title="t(pageModal)" :go-back="() => goPage('classificationTagManagement')"
            :isDisplayBackButton="pageModal != Object.keys(pages)[0]">
            <transition name="slide" mode="out-in">
                <!-- <KeepAlive> -->
                <component :is="pages[pageModal]" :key="pageModal" :goPage="goPage" />
                <!-- </KeepAlive> -->
            </transition>
        </modal>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { chevronDownOutline, ellipsisHorizontalOutline } from 'ionicons/icons';
import { useTranslate } from '@/composables/useTranslate';
import { style } from '@/assets/tailwindcss';
import Modal from '@/components/Modal/Modal.vue';
import { useClassificationCardStore } from '@/stores/classificationCard.storage';
import ClassificationCards from '@/views/ClassificationCard/ClassificationCards.vue';
import AddOrUpdateClassificationCard from '@/views/ClassificationCard/AddOrUpdateClassificationCard.vue';

const { t } = useTranslate()
const classificationCardStorage = useClassificationCardStore()
const classificationTagRef = ref<any>(null)
const popoverRef = ref<any>(null);
const selectedTagIds = ref<number[]>([]);

const pageModal = ref<ComponentKey>("classificationTagManagement")
const pages = {
    classificationTagManagement: ClassificationCards,
    detail: AddOrUpdateClassificationCard
}

type ComponentKey = keyof typeof pages;

const filterByTag = (tagId: number) => {
    const index = selectedTagIds.value.indexOf(tagId);
    if (index > -1) {
        // Nếu đã có thì xóa đi (uncheck)
        selectedTagIds.value.splice(index, 1);
    } else {
        // Nếu chưa có thì thêm vào (check)
        selectedTagIds.value.push(tagId);
    }

    // Gọi logic filter dữ liệu của bạn ở đây nếu cần
    console.log("Tags đã chọn:", selectedTagIds.value);
};

const openManager = () => {
    goPage('classificationTagManagement')
    popoverRef.value?.$el.dismiss();
    classificationTagRef.value?.present()
};

const goPage = (page: ComponentKey) => {
    pageModal.value = page
    console.log(pageModal.value)
}
</script>

<style>
/* Ghi đè biến của Ionic để dùng background tùy chỉnh từ Tailwind */
.custom-popover {
    --background: transparent;
    --box-shadow: none;
}

/* Loại bỏ padding mặc định của ion-content bên trong popover nếu có */
ion-popover::part(content) {
    border-radius: 8px;
    overflow: hidden;
}
</style>