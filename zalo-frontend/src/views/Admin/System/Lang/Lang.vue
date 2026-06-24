<template>
    <div class="p-2">
        <div class="flex justify-between items-center mb-2">
            <div class="">
                <Search v-model="search" :placeholder="`${t('search')} ...`" rounded="rounded-md" height="h-10"
                    text-size="text-lg" icon-left="left-2.5" icon-right="right-2.5" pxContent="px-9" />
            </div>

            <ion-button size="small" class="h-10" color="success" @click="router.push('/admin/system/lang/add')"><i
                    class="fa fa-plus"></i></ion-button>
        </div>
        <PaginationTable :data="langStore.languages" :columns="columns" />
    </div>
</template>

<script setup lang="ts">
import Search from "@/components/Shared/Search/Search.vue"
import PaginationTable from "@/components/Shared/Table/PaginationTable.vue"
import { useConfirmStore } from "@/composables/useConfirm"
import { useDebounce } from "@/composables/useDebounce"
import { useTranslate } from "@/composables/useTranslate"
import { useLangStore } from "@/stores/Admin/lang.storage"
import { LangType } from "@/types/entities"
import { IonButton } from "@ionic/vue"
import { ref, h, onMounted, computed, watch } from "vue"
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
                        onClick: () => router.push(`/admin/system/lang/${data.id}`)
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

watch(() => search.value, () => {
    handleSearch()
})

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