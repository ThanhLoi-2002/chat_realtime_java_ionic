<template>
    <div class="flex flex-col h-full bg-white dark:bg-gray-900 text-gray-800 dark:text-gray-100">
        <!-- BODY -->
        <div class="flex-1 overflow-hidden flex flex-col px-4 py-3 rounded-lg h-full">

            <!-- SEARCH -->
            <div class="mt-3 relative flex items-center">
                <i class="fa fa-search text-xs absolute left-3 top-1/2 -translate-y-1/2 flex items-center pointer-events-none"
                    :class="[style.text.muted]"></i>
                <input :placeholder="t('search')" class="w-full pl-8 pr-4 py-2 rounded-full text-sm
                 bg-gray-100 dark:bg-gray-800
                 outline-none" v-model="keyword" />
            </div>

            <div class="py-1">
                <classification-chip />
            </div>

            <!-- LIST -->
            <div class="flex-1 pr-1 h-full border-t border-slate-600">

                <div class="flex gap-2 h-full">
                    <!-- conv list: cố định chiều cao, scroll riêng -->
                    <div class="flex-1">
                        <div class="h-[80%] overflow-y-auto bg-white dark:bg-gray-900 rounded">
                            <div v-for="(group, letter) in groupedFriends" :key="letter">
                                <!-- LETTER -->
                                <div class="text-gray-500 dark:text-gray-400 text-xs md:text-sm mb-2 mt-4">
                                    {{ letter }}
                                </div>

                                <!-- CONVERSATIONS -->
                                <div v-for="conv in group" :key="conv.id"
                                    class="flex items-center gap-3 py-2 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800 rounded pl-2"
                                    @click="selectConv(conv)">
                                    <span
                                        class="w-4 h-4 rounded-full flex justify-center items-center border-slate-400 border"
                                        :class="isSelected(conv) ? 'bg-blue-400' : 'dark:bg-slate-600'"><i
                                            class="fa fa-check" v-show="isSelected(conv)" /></span>
                                    <img :src="conv.avatar.url" class="w-10 h-10 rounded-full object-cover" />
                                    <span class="truncate text-base">{{ conv.username }}</span>
                                </div>
                            </div>

                        </div>
                    </div>

                    <!-- Selected users list: cũng có scroll riêng; ẩn nếu rỗng -->
                    <div v-if="selectedConvs.length > 0" class="w-40">
                        <span class="dark:text-slate-400 text-sm">{{ t('selected') }} {{ selectedConvs.length
                            }}/100</span>
                        <div class="h-[85%] overflow-y-auto bg-white dark:bg-gray-900 rounded flex flex-col gap-2 mt-2">
                            <div v-for="conv in selectedConvs" :key="conv.id"
                                class="flex items-center justify-between gap-2 py-1 cursor-pointer bg-blue-500 rounded-full px-2">
                                <div class="flex gap-2 items-center">
                                    <img :src="conversationAvatar(conv)" class="w-6 h-6 rounded-full object-cover" />
                                    <span class="truncate text-sm">{{ conversationName(conv) }}</span>
                                </div>
                                <i class="fa fa-close mr-2 dark:hover:text-slate-300 text-sm"
                                    @click="removeConv(conv)"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- FOOTER -->
        <div class="flex justify-end gap-3 px-4 py-3 border-t border-gray-200 dark:border-gray-700">
            <button
                class="px-4 py-2 bg-gray-200 hover:bg-slate-100 dark:hover:bg-slate-600 dark:bg-gray-700 rounded cursor-pointer"
                @click="dismiss">
                {{ t("cancel") }}
            </button>
            <ion-button class="rounded cursor-pointer" :disabled="selectedConvs.length < 2 || groupName.length == 0"
                @click="add">
                {{ t("add") }}
            </ion-button>
        </div>

    </div>
</template>

<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import ClassificationChip from '@/components/Chip/ClassificationChip.vue';
import { useConversation } from '@/composables/useConversation';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { ConversationType, UserType } from '@/types/entities';
import { normalizeText } from '@/utils/helper';
import { computed, inject, ref } from 'vue';

const dismiss = inject<() => void>("modalDismiss")
const { t } = useTranslate()
const keyword = ref("");
const groupName = ref("");
const convStorage = useConversationStore()
const { conversationAvatar, conversationName, } = useConversation()
const convs = ref<ConversationType[]>([

])

const selectedConvs = ref<ConversationType[]>([
])

// filter
const filtered = computed(() =>
    convStorage.conversations.filter((f) =>
        normalizeText(conversationName(f)!)
            .toLowerCase()
            .includes(normalizeText(keyword.value).toLowerCase()),
    ),
);

const groupedFriends = computed(() => {
    const groups: any = {};

    filtered.value.forEach((u) => {
        const letter = conversationName(u)!.charAt(0).toUpperCase();
        if (!groups[letter]) groups[letter] = [];
        groups[letter].push(u);
    });

    return groups;
});

const isSelected = (conv: ConversationType) => {
    return selectedConvs.value.some(u => u.id === conv.id)
}

const selectConv = (conv: ConversationType) => {
    const exists = isSelected(conv)
    if (!exists) {
        selectedConvs.value.push(conv)
    } else {
        removeConv(conv)
    }
}

const removeConv = (conv: ConversationType) => {
    selectedConvs.value = selectedConvs.value.filter(u => u.id !== conv.id)
}

const add = async () => {
    const isSuccess = await convStorage.createGroup({
        name: groupName.value,
        participantIds: selectedConvs.value.map(u => u.id)
    })
    isSuccess && dismiss?.()
}
</script>