<script setup lang="ts">
import { ref, watch } from 'vue';
import { onClickOutside } from '@vueuse/core';
import {
    IonIcon,
} from '@ionic/vue';
import { addOutline, checkmarkOutline } from 'ionicons/icons';
import { useTranslate } from '@/composables/useTranslate';
import { useClassificationCardStore } from '@/stores/classificationCard.storage';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';
import { ConversationType } from '@/types/entities';
import Modal from '@/components/Modal/Modal.vue';
import AddDialogue from './component/AddDialogue.vue';

const props = defineProps<{
    goPage: (value: "classificationTagManagement" | "detail") => void
}>()

const { t } = useTranslate()
// Khai báo kiểu dữ liệu
interface ColorOption {
    id: number;
    hex: string;
}

const colorPickerRef = ref(null);
const tagTitle = ref('');
const classCardStorage = useClassificationCardStore()
const showColorPicker = ref(false);
const selectedColor = ref('#EF4444'); // Màu đỏ mặc định
const isLoading = ref(false)
const selectedConvs = ref<ConversationType[]>([])
const isUpdate = ref(false)
const modalRef = ref()

const colors: ColorOption[] = [
    { id: 1, hex: '#EF4444' }, // Red
    { id: 2, hex: '#EC4899' }, // Pink
    { id: 3, hex: '#F97316' }, // Orange
    { id: 4, hex: '#EAB308' }, // Yellow
    { id: 5, hex: '#10B981' }, // Green
    { id: 6, hex: '#06B6D4' }, // Cyan
    { id: 7, hex: '#3B82F6' }, // Blue
    { id: 8, hex: '#8B5CF6' }, // Purple
];

const toggleColorPicker = () => {
    showColorPicker.value = !showColorPicker.value;
};

const selectColor = (color: string) => {
    selectedColor.value = color;
    showColorPicker.value = false;
};

onClickOutside(colorPickerRef, () => {
    showColorPicker.value = false;
});

const handleClassificationCard = async () => {
    isLoading.value = true

    let success = false
    if (isUpdate.value && classCardStorage.card) {
        success = await classCardStorage.update(classCardStorage.card.id, {
            color: selectedColor.value,
            name: tagTitle.value,
            conversationIds: selectedConvs.value.map(c => c.id),
            position: classCardStorage.card.position
        });
    } else {
        success = await classCardStorage.create({
            color: selectedColor.value,
            name: tagTitle.value,
            conversationIds: selectedConvs.value.map(c => c.id),
            position: (classCardStorage.cards.at(-1)?.position ?? 0) + 1
        });
    }

    if (success) {
        props.goPage('classificationTagManagement')
    }

    isLoading.value = false
}

watch(
    () => classCardStorage.card,
    (newVal) => {
        if (newVal) {// update
            tagTitle.value = newVal.name
            selectedColor.value = newVal.color
            isUpdate.value = true
        } else {// create
            tagTitle.value = ''
            selectedColor.value = '#EF4444'
            selectedConvs.value = []
            isUpdate.value = false
        }
    },
    { deep: true, immediate: true }
)
</script>

<template>
    <div class="flex flex-col justify-center h-full bg-white dark:bg-slate-900/50">

        <div class="flex-1 relative w-full max-w-md rounded-xl overflow-visible text-slate-200">
            <div class="p-5 space-y-6">

                <div class="flex flex-col space-y-2">
                    <label class="text-sm text-slate-400">{{ t('categoryTagName') }}</label>
                    <div
                        class="relative flex items-center bg-white dark:bg-slate-900/50 border border-slate-700 rounded-lg overflow-hidden focus-within:border-blue-500 transition-all">
                        <input v-model="tagTitle" type="text"
                            class="flex-1 bg-transparent px-4 py-3 outline-none text-white"
                            placeholder="Nhập tên thẻ..." />
                        <button @click="toggleColorPicker"
                            class="m-2 w-8 h-6 rounded flex items-center justify-center transition-transform active:scale-90"
                            :style="{ backgroundColor: selectedColor, clipPath: 'polygon(0% 0%, 75% 0%, 100% 50%, 75% 100%, 0% 100%)' }">
                        </button>
                    </div>
                </div>

                <div class="flex flex-col space-y-4">
                    <label class="text-sm text-slate-400">{{ t('taggedConversations') }}</label>

                    <button
                        class="flex items-center text-blue-400 hover:text-blue-300 font-medium transition-colors cursor-pointer" @click="() => modalRef?.present()">
                        <ion-icon :icon="addOutline" class="text-xl mr-1" />
                        {{ t('addDialogue') }}
                    </button>

                    <div class="flex items-center space-x-3 bg-transparent">
                        <div class="w-10 h-10 rounded-full overflow-hidden border border-slate-700 shadow-lg">
                            <img src="https://vfiles.alphacoders.com/985/985338.jpg" alt="Avatar"
                                class="w-full h-full object-cover" />
                        </div>
                        <span class="font-medium">Buubuu Au</span>
                    </div>
                </div>
            </div>

            <Transition name="fade">
                <div v-if="showColorPicker" ref="colorPickerRef"
                    class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-50 w-72 bg-slate-100 dark:bg-slate-800 border border-slate-700 rounded-xl shadow-2xl p-4">
                    <p class="text-sm text-slate-400 mb-3">{{ t("changeCardColor") }}</p>
                    <div class="grid grid-cols-8 gap-2">
                        <button v-for="color in colors" :key="color.id" @click="selectColor(color.hex)"
                            class="w-6 h-6 rounded-md flex items-center justify-center transition-transform hover:scale-110"
                            :style="{ backgroundColor: color.hex }">
                            <ion-icon v-if="selectedColor === color.hex" :icon="checkmarkOutline"
                                class="text-white text-xs" />
                        </button>
                    </div>
                </div>
            </Transition>

        </div>

        <div class="flex justify-end space-x-3 p-4">
            <ion-button color="danger" class="" @click="goPage('classificationTagManagement')">
                {{ t('cancel') }}
            </ion-button>
            <ion-button class="" @click="handleClassificationCard">
                <span v-if="!isLoading">{{ t(!isUpdate ? 'add' : 'update') }}</span>
                <LoadingSpinner v-else />
            </ion-button>
        </div>

        <Modal ref="modalRef" :title="t('addDialogue')">
            <AddDialogue/>
        </Modal>
    </div>
</template>

<style>
/* Custom shadow cho Dark mode */
.shadow-inner-sm {
    box-shadow: inset 0 1px 2px 0 rgba(0, 0, 0, 0.5);
}
</style>