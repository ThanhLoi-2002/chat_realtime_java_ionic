<template>
    <div class="p-2">
        <div class="flex justify-between items-center mb-2">
            <div class="">
                <Search v-model="search" :placeholder="`${t('search')} ...`" rounded="rounded-md" height="h-10"
                    text-size="text-lg" icon-left="left-2.5" icon-right="right-2.5" pxContent="px-9" />
            </div>

            <ion-button v-if="getPagePath('add')" size="small" class="h-10" color="success" @click="router.push('lang/add')"><i
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
import { useStructure } from "@/composables/useStructure"
import { useTranslate } from "@/composables/useTranslate"
import { useLangStore } from "@/stores/Admin/lang.storage"
import { LangType } from "@/types/entities"
import { IonButton } from "@ionic/vue"
import { ref, h, onMounted, computed, watch } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()
const langStore = useLangStore()
const { getPagePath, getPermissionByCode } = useStructure()
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
            const buttons = [] // Tạo mảng chứa các nút bấm hợp lệ

            // 1. Kiểm tra quyền hiển thị nút Edit bằng cách lấy path theo code
            // Thay 'sys001lang_edit' bằng chính xác CODE của menu Edit từ backend của bạn
            const editPath = getPagePath('edit')

            if (editPath) {
                // Nếu có path (tức là có quyền), push nút Edit vào mảng
                buttons.push(
                    h(
                        IonButton,
                        {
                            size: "small",
                            color: "primary",
                            // Sử dụng chính editPath động tìm được từ menu thay vì hardcode chuỗi 'lang/edit'
                            onClick: () => router.push(`${editPath}?id=${data.id}`)
                        },
                        () => t("edit")
                    )
                );
            }

            if (getPermissionByCode('delete')) {
                buttons.push(
                    h(
                        IonButton,
                        {
                            size: "small",
                            color: "danger",
                            onClick: () => openModal(data)
                        },
                        () => t("delete")
                    )
                );
            }

            // Trả về container chứa mảng các button đã lọc quyền
            return h("div", { class: "flex justify-center gap-2" }, buttons)
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