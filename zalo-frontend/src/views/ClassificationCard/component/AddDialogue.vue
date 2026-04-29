<template>
    <div class="flex flex-col h-full bg-white dark:bg-gray-900 text-gray-800 dark:text-gray-100 overflow-hidden">
        <div class="flex-1 flex flex-col px-4 py-3 overflow-hidden">

            <div class="relative flex items-center">
                <i
                    class="fa fa-search text-xs absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none opacity-50"></i>
                <input v-model="keyword" :placeholder="t('search')"
                    class="w-full pl-8 pr-4 py-2 rounded-full text-sm bg-gray-100 dark:bg-gray-800 outline-none" />
            </div>

            <div class="py-2">
                <classification-chip />
            </div>

            <div class="flex-1 flex gap-4 min-h-0 border-t border-gray-200 dark:border-slate-700 pt-3">

                <div class="flex-1 overflow-y-auto pr-2 custom-scrollbar">
                    <div v-for="(letters, type) in groupedConvs" :key="type" class="mb-6">
                        <div
                            class="sticky top-0 bg-white dark:bg-gray-900 z-10 py-1 text-xs font-bold uppercase tracking-wider text-blue-500">
                            {{ type === ConversationEnum.GROUP ? t('Groups') : t('Private') }}
                        </div>

                        <div v-for="(items, letter) in letters" :key="letter">
                            <div class="text-gray-400 text-[12px] font-bold px-2">{{ letter }}</div>

                            <div v-for="conv in items" :key="conv.id"
                                class="flex items-center gap-3 py-2 px-2 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg transition-colors"
                                @click="selectConv(conv)">

                                <div class="shrink-0 w-4 h-4 rounded-full flex justify-center items-center border border-slate-400"
                                    :class="isSelected(conv) ? 'bg-blue-500 border-blue-500 text-white' : 'dark:bg-slate-700'">
                                    <i v-if="isSelected(conv)" class="fa fa-check text-[10px]" />
                                </div>

                                <div class="shrink-0">
                                    <circle-avatar v-if="conv.type == ConversationEnum.PRIVATE"
                                        :user="getRecipient(conv)" :is-disabled="true" size="size-9" />
                                    <group-avatar v-else :conversation="conv" :is-disabled="true" size="size-9" />
                                </div>

                                <span class="truncate text-sm flex-1">{{ conversationName(conv) }}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div v-if="selectedConvs.length > 0"
                    class="w-44 flex flex-col border-l border-gray-100 dark:border-slate-800 pl-4">
                    <div class="flex justify-between items-center mb-2">
                        <span class="text-xs font-medium text-gray-500 uppercase">{{ t('selected') }}</span>
                        <span class="text-xs bg-blue-100 text-blue-600 px-1.5 py-0.5 rounded">{{ selectedConvs.length
                        }}/100</span>
                    </div>

                    <div class="flex-1 overflow-y-auto flex flex-col gap-2 pr-1 custom-scrollbar">
                        <div v-for="conv in selectedConvs" :key="conv.id"
                            class="flex items-center justify-between gap-2 py-1 px-2 bg-blue-500 text-white rounded-full min-w-0">

                            <div class="flex items-center gap-2 min-w-0">
                                <div class="shrink-0">
                                    <circle-avatar v-if="conv.type == ConversationEnum.PRIVATE"
                                        :user="getRecipient(conv)" :is-disabled="true" size="size-6" />
                                    <group-avatar v-else :conversation="conv" :is-disabled="true" size="size-6" />
                                </div>
                                <span class="truncate text-sm">{{ conversationName(conv) }}</span>
                            </div>

                            <i class="fa fa-close text-xs cursor-pointer hover:text-red-200 shrink-0"
                                @click="removeConv(conv)"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div
            class="flex justify-end gap-3 px-6 py-4 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50">
            <ion-button color="danger" @click="dismiss">
                {{ t("cancel") }}
            </ion-button>
            <ion-button class="" :disabled="selectedConvs.length < 1" @click="add">
                {{ t("add") }}
            </ion-button>
        </div>
    </div>
</template>

<script setup lang="ts">
// ... các imports giữ nguyên ...
import { style } from '@/assets/tailwindcss';
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue';
import ClassificationChip from '@/components/Chip/ClassificationChip.vue';
import { useConversation } from '@/composables/useConversation';
import { useTranslate } from '@/composables/useTranslate';
import { useClassificationCardStore } from '@/stores/classificationCard.storage';
import { useConversationStore } from '@/stores/conversation.storage';
import { ConversationType } from '@/types/entities';
import { ConversationEnum } from '@/types/enum'
import { normalizeText } from '@/utils/helper';
import { computed, inject, onMounted, ref } from 'vue';

const selectedConvs = defineModel<ConversationType[]>({ default: [] });

const dismiss = inject<() => void>("modalDismiss")
const { t } = useTranslate()
const keyword = ref("");
const convStorage = useConversationStore()
const classCardStorage = useClassificationCardStore()
const { conversationName, getRecipient } = useConversation()

// Filter logic
const filtered = computed(() => {
    const search = normalizeText(keyword.value).toLowerCase();
    const activeId = classCardStorage.activeCardId; // Lấy ID đang chọn từ store

    return convStorage.conversations.filter((f) => {
        const name = conversationName(f) || "";
        const matchesSearch = normalizeText(name).toLowerCase().includes(search);

        // 2. Lọc theo Tag (Classification Card)
        let matchesTag = true;
        if (activeId !== 0) { // Nếu activeId = 0 là "Tất cả"
            // Tìm card tương ứng trong store
            const currentCard = classCardStorage.cards.find(c => c.id === activeId);
            // Kiểm tra xem convId có nằm trong danh sách conversationIds của card đó không
            matchesTag = currentCard?.conversationIds?.includes(f.id) || false;
        }

        return matchesSearch && matchesTag;
    });
});

// Grouping logic: Type -> Chữ cái đầu
const groupedConvs = computed(() => {
    const sorted = [...filtered.value].sort((a, b) =>
        (conversationName(a) || "").localeCompare(conversationName(b) || "")
    );

    const groups: any = {
        [ConversationEnum.PRIVATE]: {},
        [ConversationEnum.GROUP]: {}
    };

    sorted.forEach((conv) => {
        let type = conv.type; // GROUP hoặc PRIVATE

        if (conv.type == ConversationEnum.COMMUNITY) type = ConversationEnum.GROUP

        const letter = (conversationName(conv) || "#").charAt(0).toUpperCase();

        if (!groups[type]) groups[type] = {};
        if (!groups[type][letter]) groups[type][letter] = [];

        groups[type][letter].push(conv);
    });

    // 3. Xóa các key rỗng nếu không có dữ liệu để tránh render header thừa ở template
    if (Object.keys(groups[ConversationEnum.PRIVATE]).length === 0) delete groups[ConversationEnum.PRIVATE];
    if (Object.keys(groups[ConversationEnum.GROUP]).length === 0) delete groups[ConversationEnum.GROUP];

    return groups;
});

const isSelected = (conv: ConversationType) => selectedConvs.value.some(u => u.id === conv.id)

const selectConv = (conv: ConversationType) => {
    const index = selectedConvs.value.findIndex(u => u.id === conv.id)
    if (index === -1) {
        if (selectedConvs.value.length < 100) selectedConvs.value.push(conv)
    } else {
        selectedConvs.value.splice(index, 1)
    }
}

const removeConv = (conv: ConversationType) => {
    selectedConvs.value = selectedConvs.value.filter(u => u.id !== conv.id)
}

const add = async () => {
    dismiss?.()
}

onMounted(() => {
    classCardStorage.setActiveCard(0)
})
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
    width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
    background: rgba(156, 163, 175, 0.3);
    border-radius: 10px;
}
</style>