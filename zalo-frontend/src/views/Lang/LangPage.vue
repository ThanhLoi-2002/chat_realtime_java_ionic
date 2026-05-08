<template>
    <div class="p-6">
        <div class="flex justify-between items-center">
            <div class="flex items-center gap-2 mb-3
            bg-white dark:bg-gray-800
            border border-gray-300 dark:border-gray-600
            rounded-lg px-3 py-1
            focus-within:ring-2 focus-within:ring-blue-500
            w-62.5">
                <i class="fa-solid fa-search text-gray-400"></i>

                <IonInput v-model="search" :placeholder="t('search') + ' ...'" class="flex-1 search-input"  @input="handleSearch"/>
            </div>
            <ion-button size="small" class="h-10" color="success" @click="router.push('/languages/add')"><i
                    class="fa fa-plus"></i></ion-button>
        </div>
        <PaginationTable :data="langStore.languages" :columns="columns" />
    </div>
</template>

<script setup lang="ts">
import PaginationTable from "@/components/Table/PaginationTable.vue"
import { useConfirmStore } from "@/composables/useConfirm"
import { useDebounce } from "@/composables/useDebounce"
import { useTranslate } from "@/composables/useTranslate"
import { useLangStore } from "@/stores/lang.storage"
import { LangType } from "@/types/entities"
import { IonButton, IonInput } from "@ionic/vue"
import { ref, h, onMounted, computed } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()
const langStore = useLangStore()
const confirmStore = useConfirmStore();
const { t } = useTranslate()

const search = ref("");

const columns = computed(() => [
    {
        id: "stt",
        header: t("orderNum"),
        trClass: "text-nowrap",
        cell: ({ row }: any) => row.index + 1,
        // meta: {
        //     width: "5%"
        // }
    },
    {
        accessorKey: "code",
        header: "Code",
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue())
    },
    {
        accessorKey: "vi",
        header: "vi",
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue())
    },
    {
        accessorKey: "en",
        header: "en",
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue())
    },
    {
        accessorKey: "tw",
        header: "tw",
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue())
    },
    {
        accessorKey: "cn",
        header: "cn",
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue())
    },
    {
        id: "action",
        header: "Action",
        cell: ({ row }: any) => {
            const data = row.original

            return h("div", { class: "flex justify-center gap-2" }, [
                h(
                    IonButton,
                    {
                        size: "small",
                        color: "primary",
                        onClick: () => router.push(`/languages/${data.id}`)
                    },
                    () => t("edit")
                ),

                h(
                    IonButton,
                    {
                        size: "small",
                        color: "danger",
                        onClick: () => openModal(data)
                    },
                    () => t("delete")
                )
            ])
        }
    }
])

const { debounced: handleSearch } = useDebounce(() => {
    langStore.getList(search.value)
}, 300)

const openModal = (item: LangType) => {
    confirmStore.open({
        title: t('delete'),
        message: `Bạn có chắc muốn xoá ${item.code}?`,
        onOk: () => {
            langStore.delete(item.id)
        }
    });
}

onMounted(() => {
    langStore.getList()
})
</script>

<style>
.search-input {
    --color: #374151;
    --placeholder-color: #9ca3af;
}

.dark .search-input {
    --color: #e5e7eb;
    --placeholder-color: #d1d5db;
}
</style>