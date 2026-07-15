<script setup lang="ts">
import PaginationTableApi from "@/components/Shared/Table/PaginationTableApi.vue";
import { useDebounce } from "@/composables/useDebounce";
import { useTranslate } from "@/composables/useTranslate";
import { useAdminUserStore } from "@/stores/Admin/user.storage";
import { IonButton } from "@ionic/vue";
import { ref, onMounted, computed, h, watch } from "vue";
import { useRouter } from "vue-router";

const LIMIT = 20

const { t } = useTranslate()
const userStor = useAdminUserStore()
const search = ref("");
const router = useRouter()

const columns = computed(() => [
    {
        id: "stt",
        header: () => t("orderNum"),
        trClass: "text-nowrap",
        cell: ({ row }: any) => (row.index + 1) + (LIMIT * (userStor.list.page.number)),
        meta: {
            width: "6%"
        }
    },
    {
        accessorKey: "username",
        header: () => t('username'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue()),
        meta: {
            width: "10%"
        }
    },
    {
        accessorKey: "phone",
        header: () => t('phone'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue()),
        meta: {
            width: "10%"
        }
    },
    {
        id: "action",
        header: () => t('action'),
        cell: ({ row }: any) => {
            const data = row.original

            return h("div", { class: "flex justify-center gap-2" }, [
                h(
                    IonButton,
                    {
                        size: "small",
                        color: "primary",
                        onClick: () => {
                            router.push(`user/edit?id=${data.id}`)
                        }
                    },
                    () => t("edit")
                ),

                h(
                    IonButton,
                    {
                        size: "small",
                        color: "danger",
                        onClick: () => {

                        }
                    },
                    () => t("delete")
                ),
            ])
        },
        meta: {
            width: "8%"
        }
    }
])

const { debounced: handleSearch } = useDebounce(async () => {
    await userStor.getList({ limit: LIMIT, username: search.value })
}, 300)

watch(() => search.value, () => {
    handleSearch()
})

const handlePageChange = async (newPageIndex: number) => {
    await userStor.getList({ page: newPageIndex, limit: LIMIT, username: search.value })
}

onMounted(async () => {
    await userStor.getList({ limit: LIMIT })
});
</script>

<template>
    <div class="p-2">
        <div class="flex justify-between items-center mb-2">
            <div class="">
                <Search v-model="search" :placeholder="`${t('search')} ...`" rounded="rounded-md" height="h-10"
                    text-size="text-lg" icon-left="left-2.5" icon-right="right-2.5" pxContent="px-9" />
            </div>
        </div>
        <PaginationTableApi v-if="userStor.list" :data="userStor.list.content" :columns="columns" :page-size="LIMIT"
            :totalElements="userStor.list.page.totalElements" :pageIndex="userStor.list.page.number"
            @pageChange="handlePageChange" />
    </div>
</template>