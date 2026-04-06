<template>
    <!-- SECURITY -->
    <collapse v-model:isOpen="isSecurity" :title="t('setUpSecurity')">
        <div>
            <div class="dark:text-white">{{ t("messagesDeleteThemselves") }}</div>
            <div class="text-xs text-gray-400">{{ t("never") }}</div>
        </div>

        <!-- SWITCH -->
        <div class="flex justify-between items-center">
            <span class="dark:text-white">{{ t("hideChat") }}</span>

            <button @click="isHidden = !isHidden" class="w-10 h-6 flex items-center rounded-full p-1 transition"
                :class="isHidden ? 'bg-blue-500' : 'bg-gray-300 dark:bg-slate-600'">
                <div class="w-4 h-4 bg-white rounded-full shadow transition" :class="isHidden ? 'translate-x-4' : ''" />
            </button>
        </div>

        <div class="text-red-500 hover:text-red-600 cursor-pointer transition" @click="deleteConversation">
            {{ t("deleteChatHistory") }}
        </div>
    </collapse>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { ref } from 'vue';

const { t } = useTranslate()
const isSecurity = ref(true)

const deleteConversation = () => console.log('delete')

/* STATE */
const isHidden = ref(false)

</script>
