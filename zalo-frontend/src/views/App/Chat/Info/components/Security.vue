<template>
    <!-- SECURITY -->
    <collapse v-model:isOpen="isSecurity" :title="t('setUpSecurity')">
        <div class=" flex flex-col gap-3 px-4">
            <div class="flex gap-2 items-center">
                <i class="fas fa-stopwatch text-xl" :class="[style.text.primary]"></i>
                <div class="">
                    <div class="dark:text-white">{{ t("messagesDeleteThemselves") }}</div>
                    <div class="text-xs text-gray-400">{{ t("never") }}</div>
                </div>
            </div>

            <!-- SWITCH -->
            <div class="flex justify-between items-center">
                <div class="flex gap-2">
                    <i class="fas fa-eye-slash text-md" :class="[style.text.primary]"></i>
                    <span class="dark:text-white">{{ t("hideChat") }}</span>
                </div>

                <Switch v-model="isHidden" size="w-9 h-5 after:h-4 after:w-4" />
            </div>

            <div class="flex gap-2 text-red-500 hover:text-red-600 cursor-pointer transition" @click="deleteConversation">
                <i class="far fa-trash-alt text-lg"></i> {{ t("deleteChatHistory") }}
            </div>
        </div>
    </collapse>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import Switch from '@/components/Shared/Switch/Switch.vue';
import { useTranslate } from '@/composables/useTranslate';
import { ref } from 'vue';

const { t } = useTranslate()
const isSecurity = ref(true)

const deleteConversation = () => console.log('delete')

/* STATE */
const isHidden = ref(false)

</script>
