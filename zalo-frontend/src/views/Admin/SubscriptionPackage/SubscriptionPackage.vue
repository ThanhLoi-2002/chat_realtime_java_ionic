<script setup lang="ts">
import { style } from '@/assets/tailwindcss'
import Modal2 from '@/components/Shared/Modal/Modal2.vue'
import PaginationTableApi from '@/components/Shared/Table/PaginationTableApi.vue'
import { useDebounce } from '@/composables/useDebounce'
import { useTranslate } from '@/composables/useTranslate'
import { useSubscriptionPackageStore } from '@/stores/App/subscriptionPackage.storage'
import { PageType } from '@/types/common'
import { SubscriptionPackageType } from '@/types/entities'
import { appLimit, emptyPage } from '@/utils/constant'
import { IonButton } from '@ionic/vue'
import { ref, onMounted, watch, h, computed } from 'vue'
import SubscriptionPackageForm from './components/SubscriptionPackageForm.vue'
import { useConfirmStore } from '@/composables/useConfirm.ts'
import { formatCurrencyVND } from '@/utils/helper.ts'

const showModal = ref(false)
const { t } = useTranslate()
const search = ref("");
const subscriptionPackageStor = useSubscriptionPackageStore()
const data = ref<PageType<SubscriptionPackageType>>(emptyPage)
const selectedItem = ref<SubscriptionPackageType | undefined>(undefined)
const confirmStore = useConfirmStore()

// Mở modal thêm mới
const handleOpenCreate = () => {
    selectedItem.value = undefined
    showModal.value = true
}

// Mở modal chỉnh sửa
const handleOpenEdit = (e: SubscriptionPackageType) => {
    selectedItem.value = e
    showModal.value = true
}

// Xóa gói cước
const handleDelete = async (e: SubscriptionPackageType) => {
    if (!e) return
    confirmStore.open({
        title: t('delete'),
        message: `Bạn có chắc muốn xoá ${e.name}?`,
        onOk: async () => {
            await subscriptionPackageStor.delete(e.id)
            fetchList()
        }
    });
}

const columns = computed(() => [
    {
        id: "stt",
        header: () => t("orderNum"),
        trClass: "text-nowrap",
        cell: ({ row }: any) => (row.index + 1) + (appLimit.comLimit * (data.value.page.number)),
        meta: {
            width: "3%"
        }
    },
    {
        accessorKey: "name",
        header: () => t('name'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue()),
        meta: {
            width: "10%"
        }
    },
    {
        accessorKey: "price",
        header: () => t('price'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, formatCurrencyVND(getValue())),
        meta: {
            width: "5%"
        }
    },
    {
        accessorKey: "level",
        header: () => t('level'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue()),
        meta: {
            width: "5%"
        }
    },
    {
        accessorKey: "category",
        header: () => t('category'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue()),
        meta: {
            width: "5%"
        }
    },
    {
        accessorKey: "targetType",
        header: () => t('targetType'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue()),
        meta: {
            width: "5%"
        }
    },
    {
        accessorKey: "billingCycle",
        header: () => t('billingCycle'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue()),
        meta: {
            width: "5%"
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
                            handleOpenEdit(data)
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
                            handleDelete(data)
                        }
                    },
                    () => t("delete")
                ),
            ])
        },
        meta: {
            width: "3%"
        }
    }
])

const { debounced: handleSearch } = useDebounce(async () => {
    await subscriptionPackageStor.getAll({ limit: appLimit.comLimit, name: search.value })
}, 300)

watch(() => search.value, () => {
    handleSearch()
})

const handlePageChange = async (newPageIndex: number) => {
    await subscriptionPackageStor.getAll({ page: newPageIndex, limit: appLimit.comLimit, name: search.value })
}

const closeModal = () => {
    showModal.value = false
}

const fetchList = async () => {
    data.value = await subscriptionPackageStor.getAll({ stt: 1 })
}

onMounted(async () => {
    fetchList()
})
</script>

<template>
    <div :class="['p-2']">
        <!-- Header -->
        <div class="flex justify-between items-center mb-8">
            <div>
                <div :class="[style.text.primary, 'text-2xl']">{{ t('subscriptionPackagesManage') }}</div>
                <p :class="[style.text.secondary, 'text-sm']">{{ t('Quản lý các gói cước') }}
                </p>
            </div>
            <ion-button @click="handleOpenCreate" size="small" class="h-8 space-x-2" color="primary"><i
                    class="fa fa-plus"></i>
                <p>{{ t('add') }}</p>
            </ion-button>
        </div>

        <PaginationTableApi v-if="data.content" :data="data.content" :columns="columns" :page-size="appLimit.comLimit"
            :totalElements="data.page.totalElements" :pageIndex="data.page.number" @pageChange="handlePageChange" />

        <Modal2 v-model="showModal" :close-on-backdrop="true" @close="closeModal" max-w="max-w-2xl"
            :title="selectedItem ? t('update') : t('add')">
            <SubscriptionPackageForm @close="closeModal" :selectedItem="selectedItem" @fetch-list="fetchList" />
        </Modal2>
    </div>
</template>