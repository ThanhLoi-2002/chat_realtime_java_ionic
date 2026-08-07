<script setup lang="ts">
import CircleAvatar from "@/components/Shared/Avatar/CircleAvatar.vue";
import PaginationTableApi from "@/components/Shared/Table/PaginationTableApi.vue";
import { useConfirmStore } from "@/composables/useConfirm";
import { useDebounce } from "@/composables/useDebounce";
import { useTranslate } from "@/composables/useTranslate";
import { useOaStore } from "@/stores/Admin/oa.storage";
import { PageType } from "@/types/common";
import { OaType } from "@/types/entities";
import { OaStatusEnum } from "@/types/enum";
import { MINIO_URL } from "@/utils/constant";
import { IonButton } from "@ionic/vue";
import { ref, onMounted, computed, h, watch } from "vue";

const LIMIT = 20

const { t } = useTranslate()
const search = ref("");
const oaStor = useOaStore()
const list = ref<PageType<OaType>>()
const confirmStore = useConfirmStore();

// State cho Form bên phải
const id = ref<number | undefined>()

const columns = computed(() => [
    {
        id: "stt",
        header: () => t("orderNum"),
        trClass: "text-nowrap",
        cell: ({ row }: any) => row.index + 1,
        // meta: {
        //     width: "5%"
        // }
    },
    {
        accessorKey: "name",
        header: () => t('name'),
        cell: ({ row }: any) => {
            const avatarPath = row.original.avatar;

            return h("div", { class: "flex items-center gap-2" }, [
                // Truyền trực tiếp Component object thay vì string "circle-avatar"
                h(CircleAvatar, {
                    src: avatarPath ? `${MINIO_URL}/${avatarPath}` : undefined,
                    size: "size-7",
                    class: "shrink-0"
                }),
                h("span", { class: "" }, row.getValue("name")),
            ]);
        },
        // meta: {
        //     width: "20%"
        // }
    },
    {
        accessorKey: "code",
        header: () => t('code'),
        cell: ({ getValue }: any) =>
            h("span", { class: "" }, getValue()),
        // meta: {
        //     width: "15%"
        // }
    },
    {
        accessorKey: "categoryName",
        header: () => t('category'),
        cell: ({ getValue }: any) =>
            h("span", { class: "" }, getValue()),
    },
    {
        accessorKey: "phone",
        header: () => t('phone'),
        cell: ({ getValue }: any) =>
            h("span", { class: "" }, getValue()),
    },
    {
        accessorKey: "status",
        header: () => t('status'),
        cell: ({ getValue }: any) =>
            h("span", { class: "" }, getValue()),
    },
    {
        id: "action",
        header: () => t('action'),
        cell: ({ row }: any) => {
            const data = row.original;

            // Tạo mảng chứa các nút bấm động
            const buttons: any[] = [];

            // 1. Chỉ hiện nút active/sửa nếu status là PENDING (hoặc điều kiện của bạn)
            if (data.status === OaStatusEnum.PENDING || data.status === OaStatusEnum.DELETED) {
                buttons.push(
                    h(
                        IonButton,
                        {
                            size: "small",
                            color: "primary",
                            onClick: () => {
                                openModal(data, OaStatusEnum.ACTIVE)
                            }
                        },
                        () => t("acitve") // Lưu ý: t("active") nếu code gốc của bạn bị typo chữ acitve
                    )
                );
            }

            if (data.status !== OaStatusEnum.DELETED) {
                // 2. Nút delete luôn luôn được push vào mảng (Luôn hiển thị)
                buttons.push(
                    h(
                        IonButton,
                        {
                            size: "small",
                            color: "danger",
                            onClick: () => {
                                openModal(data, OaStatusEnum.DELETED)
                            }
                        },
                        () => t("delete")
                    )
                );
            }

            return h("div", { class: "flex justify-center gap-2" }, buttons);
        },
        meta: {
            width: "8%"
        }
    }
])

const openModal = (item: OaType, status: OaStatusEnum) => {
    let title = '';
    let message = '';

    switch (status) {
        case OaStatusEnum.ACTIVE:
            title = 'active'
            message = `Bạn có chắc muốn active ${item.name} - ${item.code}?`
            break
        case OaStatusEnum.DELETED:
            title = 'delete'
            message = `Bạn có chắc muốn xoá ${item.name} - ${item.code}?`
            break
    }
    confirmStore.open({
        title: t(title),
        message,
        onOk: async () => {
            const oa = await oaStor.updateStatus(item.id, status)
            list.value!.content = list.value!.content.map(e =>
                e.id === item.id ? oa : e
            );
        }
    });
}

const handlePageChange = async (newPageIndex: number) => {
    list.value = await oaStor.getAll({ page: newPageIndex, limit: LIMIT, search: search.value })
}

const { debounced: handleSearch } = useDebounce(async () => {
    list.value = await oaStor.getAll({ limit: LIMIT, search: search.value })
}, 300)

watch(() => search.value, () => {
    handleSearch()
})

onMounted(async () => {
    list.value = await oaStor.getAll({ limit: LIMIT })
});
</script>

<template>
    <div class="">
        <!-- Cột bên trái: Bảng dữ liệu và Tìm kiếm -->
        <div class="flex justify-between items-center mb-4">
            <div>
                <Search v-model="search" :placeholder="`${t('search')} ...`" rounded="rounded-md" height="h-10"
                    text-size="text-lg" icon-left="left-2.5" icon-right="right-2.5" pxContent="px-9" />
            </div>
        </div>

        <PaginationTableApi v-if="list" :data="list.content" :columns="columns" :page-size="LIMIT"
            :totalElements="list.page.totalElements" :pageIndex="list.page.number" @pageChange="handlePageChange" />
    </div>
</template>