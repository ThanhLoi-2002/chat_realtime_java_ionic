<template>
    <div class="h-full flex flex-col">

        <!-- HEADER -->
        <div class="p-4 flex items-center gap-3 border-b dark:border-slate-700" :class="[style.text.secondary]">
            <i class="fa fa-arrow-left cursor-pointer" @click="$emit('back')"></i>
            <span class="font-semibold m-auto">{{ t("generalGroup") }}</span>
        </div>

        <!-- LIST -->
        <div class="flex-1 overflow-y-auto p-3">
            <div class="flex items-center gap-3 p-2 hover:bg-gray-100 dark:hover:bg-slate-800 rounded cursor-pointer"
                v-for="c in convStorage.generalGroup" :key="c.id">
                <GroupAvatar :conversation="c" size="w-12 h-12"/>
                <div class="flex-1">
                    <div class="text-sm font-medium dark:text-white">
                        {{ c.name }}
                    </div>
                </div>
            </div>
        </div>

        <!-- FOOTER -->
        <div class="p-3 border-t" :class="[style.border.primary]">
            <button class="w-full py-2 rounded cursor-pointer" :class="[style.button.base]">
                ➕ {{ t("addToGroup") }}
            </button>
        </div>

    </div>
</template>

<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';

defineEmits(['back'])
const { t } = useTranslate()
const convStorage = useConversationStore()
</script>