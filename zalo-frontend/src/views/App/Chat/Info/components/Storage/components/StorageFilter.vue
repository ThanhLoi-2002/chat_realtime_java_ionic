<script setup lang="ts">
import { computed, reactive, watch } from "vue";
import { useDebounceFn } from "@vueuse/core";

import FilterSender from "./FilterSender.vue";
import FilterDate from "./FilterDate.vue";
import { MessageFilter } from "@/types/common.ts";
import FilterFileType from "./FilterFileType.vue";
import Search from "@/components/Shared/Search/Search.vue";
import { useTranslate } from "@/composables/useTranslate.ts";
import { useConversationStore } from "@/stores/App/conversation.storage.ts";
import { useConversation } from "@/composables/useConversation.ts";

const props = defineProps<{
    showSearch: boolean
    showFileType: boolean
}>()

const emit = defineEmits<{
    change: [any];
}>();

const { t } = useTranslate()
const convStorage = useConversationStore()
const { isGroup } = useConversation()

const filter = reactive<MessageFilter>({
    conversationId: convStorage.conversation!.id,
    keyword: "",
    fileType: "",
    senderId: 0,
    dateRange: [null, null],
});

const members = computed(() => {
    if(!isGroup(convStorage.conversation)) return []
    return convStorage.conversation?.members || []
})

const emitChange = useDebounceFn(() => {
    emit("change", { ...filter });
}, 500);

watch(filter, () => emitChange(), { deep: true });
</script>

<template>
    <div class="space-y-2 px-1 py-2">
        <Search v-if="showSearch" v-model="filter.keyword" :placeholder="t('search')" rounded="rounded-4xl" height="h-7"
            text-size="text-md" icon-left="left-3" icon-right="right-2.5" pxContent="px-8" />

        <div class="flex gap-2 justify-between">
            <FilterFileType v-if="showFileType" v-model="filter.fileType" />

            <FilterSender v-model="filter.senderId" :users="members"/>

            <FilterDate v-model="filter.dateRange" />
        </div>
    </div>
</template>