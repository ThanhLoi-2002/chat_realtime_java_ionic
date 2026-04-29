<template>
    <div v-if="isDisplay" :id="id" @click="onClick" class="flex gap-2 items-center cursor-pointer">
        <div class="w-5 h-3 rounded flex items-center justify-center transition-transform active:scale-90 cursor-pointer"
            :style="{ backgroundColor: getClassCard(conv?.id)?.color || color || '#D1D5DB', clipPath: 'polygon(0% 0%, 75% 0%, 100% 50%, 75% 100%, 0% 100%)' }">
        </div>
        <slot />
    </div>

</template>
<script setup lang="ts">
import { useClassCard } from '@/composables/useClassCard';
import { useClassificationCardStore } from '@/stores/classificationCard.storage';
import { ConversationType } from '@/types/entities';
import { ref, watch } from 'vue';

const props = defineProps<{
    conv?: ConversationType
    color?: string
    id?: string
    onClick?: () => void
    isChatHeader?: boolean
}>()

const classCardStorage = useClassificationCardStore()

const { getClassCard } = useClassCard()

const checkDisplay = () => getClassCard(props.conv?.id)?.color || props.isChatHeader || props.color

const isDisplay = ref(checkDisplay())

watch(() => classCardStorage.cards, () => {
    isDisplay.value = checkDisplay()
}, { deep: true })
</script>