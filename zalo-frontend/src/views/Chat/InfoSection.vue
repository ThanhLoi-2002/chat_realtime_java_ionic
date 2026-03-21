<template>
    <div class="p-4 font-semibold dark:text-white flex gap-2">
        <div class="sm:hidden"><mobile-back-button :onClick="() => emit('update:isShowInfoSection', !isShowInfoSection)" /></div>
        
        <span class="">Info</span>
    </div>

    <div class="p-4">
        <!-- ví dụ -->
        <circle-avatar :url="RANDOM_AVATAR" size="size-16" :onClick="() => { }" />

        <div class="mt-3 dark:text-slate-200">
            {{ conversationStorage.recipient?.username }}
        </div>
    </div>
</template>
<script setup lang="ts">
import { useConversationStore } from '@/stores/conversation.storage';
import { RANDOM_AVATAR } from '@/utils/constant';
import MobileBackButton from '@/components/Button/MobileBackButton.vue';

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const conversationStorage = useConversationStore()

const emit = defineEmits(['update:isShowInfoSection'])
</script>
