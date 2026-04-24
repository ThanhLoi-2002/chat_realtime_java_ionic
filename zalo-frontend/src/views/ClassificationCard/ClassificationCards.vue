<script setup lang="ts">
import { ref, computed } from 'vue';
import draggable from 'vuedraggable';
import { IonIcon, IonButton } from '@ionic/vue';
import { menuOutline, add } from 'ionicons/icons';
import { useTranslate } from '@/composables/useTranslate';
import ConfirmModal from '@/components/Modal/ConfirmModal.vue';
import { ClassificationCardType } from '@/types/entities';
import { useClassificationCardStore } from '@/stores/classificationCard.storage';

const props = defineProps<{
    goPage: (value: "classificationTagManagement" | "detail") => void
}>()

const { t } = useTranslate()
const classificationCardStorage = useClassificationCardStore()
const showConfirm = ref(false)
const selectedItem = ref<ClassificationCardType | undefined>(undefined)

/**
 * SỬ DỤNG COMPUTED VỚI GET/SET
 * Đây là chìa khóa để vuedraggable hoạt động mượt mà với Store.
 */
const dragList = computed({
    get: () => classificationCardStorage.cards,
    set: (val) => {
        // Cập nhật lại mảng trong store kèm theo đánh lại số position
        classificationCardStorage.cards = val.map((item, index) => ({
            ...item,
            position: index + 1 // STT bắt đầu từ 1
        }));

        // Gọi API lưu vị trí mới ở đây (nếu cần)
        classificationCardStorage.updatePositions();
    }
});

const onOk = async () => {
    if (selectedItem.value) {
        const success = await classificationCardStorage.delete(selectedItem.value.id)
        if (success) showConfirm.value = false;
    }
}

const onSelectItemDelete = (item: ClassificationCardType) => {
    selectedItem.value = item
    showConfirm.value = true;
}

const onEdit = (item: ClassificationCardType) => {
    classificationCardStorage.selectCard(item)
    props.goPage('detail')
}

const goToAddPage = () => {
    classificationCardStorage.selectCard()
    props.goPage('detail')
}
</script>

<template>
    <div class="flex flex-col h-full bg-white dark:bg-slate-900/50 text-slate-900 dark:text-slate-200 p-4">

        <div class="flex-1 min-h-0 overflow-y-auto pr-2">
            <draggable 
                v-model="dragList" 
                item-key="id"
                handle=".drag-handle"
                :animation="300"
                tag="div"
                class="space-y-3 pb-4"
                ghost-class="sortable-placeholder"
                chosen-class="sortable-chosen"
                drag-class="sortable-drag"
            >
                <template #item="{ element: tag }">
                    <div class="flex items-center group bg-slate-50 dark:bg-[#1c1f24] border border-slate-200 dark:border-slate-800 rounded-xl px-2 py-2 shadow-sm">
                        
                        <div class="drag-handle cursor-grab active:cursor-grabbing p-2 text-slate-400 hover:text-indigo-500 transition-colors">
                            <ion-icon :icon="menuOutline" class="text-2xl" />
                        </div>

                        <div class="flex-1 flex items-center bg-white dark:bg-[#282c33] py-2.5 px-4 rounded-lg shadow-inner-sm ml-1 group-hover:ring-1 group-hover:ring-indigo-500/30 transition-all">
                            <div class="w-5 h-5 rounded-full mr-3 border-2 border-white dark:border-slate-700 flex-none shadow-sm"
                                :style="{ backgroundColor: tag.color }">
                            </div>

                            <span class="flex-1 font-semibold text-sm truncate">{{ tag.name }}</span>

                            <div class="flex items-center space-x-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                <button @click.stop="onEdit(tag)" class="p-2 hover:text-blue-500 transition-colors">
                                    <i class="fa fa-edit"></i>
                                </button>
                                <button @click.stop="onSelectItemDelete(tag)" class="p-2 hover:text-red-500 transition-colors">
                                    <i class="fa fa-trash"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </template>
            </draggable>
        </div>

        <div class="pt-4 border-t border-slate-200 dark:border-slate-800">
            <ion-button @click="goToAddPage" expand="block" class="ion-margin-top">
                <ion-icon slot="start" :icon="add"></ion-icon>
                {{ t("addClassification") }}
            </ion-button>
        </div>

        <confirm-modal 
            :showConfirm="showConfirm" 
            :header="t('confirmDelete')" 
            :message="t('deleteMessage')" 
            :onOk="onOk" 
            @close="showConfirm = false"
        />
    </div>
</template>

<style scoped>
/* Class khi kéo: Vùng trống để lại */
.sortable-placeholder {
    opacity: 0.5;
    background: #cbd5e1 !important;
    border: 2px dashed #6366f1 !important;
    border-radius: 0.75rem;
}

/* Class của item khi đang bị kéo đi */
.sortable-drag {
    opacity: 0.9;
    transform: rotate(2deg);
    cursor: grabbing;
}

/* CSS dark mode cho placeholder */
.dark .sortable-placeholder {
    background: #1e293b !important;
    border-color: #4f46e5 !important;
}

/* Đảm bảo smooth animation */
.flip-list-move {
  transition: transform 0.5s;
}
</style>