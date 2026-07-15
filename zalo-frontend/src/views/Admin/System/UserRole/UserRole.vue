<script setup lang="ts">
import PaginationTableApi from "@/components/Shared/Table/PaginationTableApi.vue";
import { useDebounce } from "@/composables/useDebounce";
import { useTranslate } from "@/composables/useTranslate";
import { useAdminRoleStore } from "@/stores/Admin/role.storage";
import { PageType } from "@/types/common";
import { IonButton } from "@ionic/vue";
import { ref, onMounted, computed, h, watch } from "vue";
import { useRouter } from "vue-router";

const LIMIT = 20

const { t } = useTranslate()
const roleStor = useAdminRoleStore()
const search = ref("");
const page = ref<PageType<any>>()
const router = useRouter()

const columns = computed(() => [
    {
        id: "stt",
        header: () => t("orderNum"),
        trClass: "text-nowrap",
        cell: ({ row }: any) => (row.index + 1) + (LIMIT * (page.value?.page.number ?? 0)),
        meta: {
            width: "6%"
        }
    },
    {
        accessorKey: "user.username",
        header: () => t('username'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue()),
        meta: {
            width: "10%"
        }
    },
    {
        accessorKey: "user.phone",
        header: () => t('phone'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue()),
        meta: {
            width: "10%"
        }
    },
    {
        accessorKey: "roles",
        header: () => t('roles'),
        cell: ({ getValue }: any) => {
            const roles = getValue() || []; // Đảm bảo luôn là mảng, tránh bị lỗi undefined

            // Trả về một div bọc ngoài, bên trong map mảng roles thành danh sách các thẻ span (Chip)
            return h("div", { class: "flex flex-wrap gap-1 whitespace-normal break-words" },
                roles.map((role: string) =>
                    h("span", {
                        class: "bg-blue-50 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300 text-xs px-2 py-0.5 rounded border border-blue-200 dark:border-blue-800/50 font-medium"
                    }, role)
                )
            );
        },
        meta: {
            width: "40%"
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
                            roleStor.setUserRole({ id: data.user.id, username: data.user.username, phone: data.user.phone, roleIds: data.roleIds })
                            router.push(`user-role/edit`)
                        }
                    },
                    () => t("edit")
                ),
            ])
        },
        meta: {
            width: "8%"
        }
    }
])

const { debounced: handleSearch } = useDebounce(async () => {
    page.value = await roleStor.getUsersRoles({ limit: LIMIT, username: search.value })
}, 300)

watch(() => search.value, () => {
    handleSearch()
})

const handlePageChange = async (newPageIndex: number) => {
    page.value = await roleStor.getUsersRoles({ page: newPageIndex, limit: LIMIT, username: search.value })
}

onMounted(async () => {
    page.value = await roleStor.getUsersRoles({ limit: LIMIT })

    if (roleStor.roles.length == 0) {
        roleStor.getList()
    }
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
        <PaginationTableApi v-if="page" :data="page.content" :columns="columns" :page-size="LIMIT"
            :totalElements="page.page.totalElements" :pageIndex="page.page.number" @pageChange="handlePageChange" />
    </div>
</template>