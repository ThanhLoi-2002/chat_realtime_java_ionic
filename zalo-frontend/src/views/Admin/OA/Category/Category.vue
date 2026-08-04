<script setup lang="ts">
import { oaStyle } from "@/assets/tailwindcss";
import BaseButton from "@/components/Shared/Button/BaseButton.vue";
import ErrorInput from "@/components/Shared/Form/ErrorInput.vue";
import PaginationTable from "@/components/Shared/Table/PaginationTable.vue";
import { useTranslate } from "@/composables/useTranslate";
import { categorySchema } from "@/schema/Oa/category.schema";
import { useOaCategoryStore } from "@/stores/Admin/oaCategory.storage";
import { OaCategoryType } from "@/types/entities";
import { normalizeText } from "@/utils/helper";
import { IonButton } from "@ionic/vue";
import { toTypedSchema } from "@vee-validate/yup";
import { useForm } from "vee-validate";
import { ref, onMounted, computed, h, watch } from "vue";

const LIMIT = 20

const { t } = useTranslate()
const search = ref("");
const categoryStor = useOaCategoryStore()
const list = ref<OaCategoryType[]>([])
const filteredList = ref<OaCategoryType[]>([])
const { handleSubmit, errors, defineField, setValues, resetForm: resetVeeForm, values } = useForm({
    validationSchema: toTypedSchema(categorySchema),
    validateOnMount: false
});
const isLoading = ref(false)

// State cho Form bên phải
const id = ref<number | undefined>()

const columns = computed(() => [
    {
        id: "stt",
        header: () => t("orderNum"),
        trClass: "text-nowrap",
        cell: ({ row }: any) => row.index + 1,
        meta: {
            width: "5%"
        }
    },
    {
        accessorKey: "name",
        header: () => t('name'),
        cell: ({ getValue }: any) =>
            h("span", { class: "" }, getValue()),
        meta: {
            width: "20%"
        }
    },
    {
        accessorKey: "code",
        header: () => t('code'),
        cell: ({ getValue }: any) =>
            h("span", { class: "" }, getValue()),
        meta: {
            width: "15%"
        }
    },
    {
        accessorKey: "description",
        header: () => t('description'),
        cell: ({ getValue }: any) =>
            h("span", { class: "" }, getValue()),
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
                            // Đưa dữ liệu vào form để sửa
                            id.value = data.id
                            setValues({
                                name: data.name,
                                code: data.code,
                                description: data.description || ""
                            })
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
                            // Xử lý xóa ở đây
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

// Reset form khi muốn tạo mới
const resetForm = () => {
    id.value = undefined
    resetVeeForm({
        values: {
            name: "",
            code: "",
            description: ""
        }
    });
}

// Xử lý Submit Form (Thêm / Sửa)
const save = handleSubmit(async (values: any) => {
    isLoading.value = true
    try {
        if (id.value) {
            // Gọi API Update ở đây
            const data = await categoryStor.update(id.value, values)
            list.value = list.value.map(item =>
                item.id === data.id ? data : item
            )
        } else {
            const data = await categoryStor.create(values)
            list.value = [data, ...list.value]
        }

        // Load lại danh sách sau khi lưu thành công
        handleSearch(search.value)
        resetForm()
    } catch (error) {
        console.error(error)
    } finally {
        isLoading.value = false
    }
})

const handleSearch = (text?: string) => {
    if (!text) filteredList.value = list.value
    else
        filteredList.value = list.value.filter((i) =>
            normalizeText(i.code).includes(normalizeText(text)) ||
            normalizeText(i.name).includes(normalizeText(text))
        )
}

watch(() => search.value, () => {
    handleSearch(search.value)
})

onMounted(async () => {
    list.value = await categoryStor.getAll()
    filteredList.value = list.value
});
</script>

<template>
    <div class="p-4 flex gap-4 items-start">
        <!-- Cột bên trái: Bảng dữ liệu và Tìm kiếm -->
        <div class="w-2/3">
            <div class="flex justify-between items-center mb-4">
                <div>
                    <Search v-model="search" :placeholder="`${t('search')} ...`" rounded="rounded-md" height="h-10"
                        text-size="text-lg" icon-left="left-2.5" icon-right="right-2.5" pxContent="px-9" />
                </div>
                <BaseButton :type="'button'" label="create" icon="fas fa-plus text-xs" @click="resetForm"
                    customClass="bg-green-500 hover:bg-green-500/90 px-1 py-0.5 rounded-sm" />
            </div>

            <PaginationTable :data="filteredList" :columns="columns" :page-size="LIMIT"/>
        </div>

        <!-- Cột bên phải: Form Thêm / Sửa -->
        <div :class="[oaStyle.border.secondary, 'w-1/3 p-4 rounded-lg border']">
            <div :class="[oaStyle.text.primary, 'text-lg font-medium mb-4']">
                {{ id ? t('edit') : t('add') }}
            </div>

            <form @submit="save" class="space-y-4">
                <ErrorInput :errors="errors" name="name" label="name" :define-field="defineField"
                    :schema="categorySchema" placeholder="name" direction="horizontal" />

                <ErrorInput :errors="errors" name="code" label="code" :define-field="defineField"
                    :schema="categorySchema" placeholder="code" direction="horizontal" />

                <ErrorInput :errors="errors" name="description" label="description" :define-field="defineField"
                    :schema="categorySchema" placeholder="description" direction="horizontal" :is-textarea="true" />

                <div class="flex justify-end gap-2 pt-2">
                    <BaseButton v-if="id" label="cancel" @click="resetForm" :disabled="isLoading"
                        customClass="bg-red-500 hover:bg-red-500/90 px-2 py-0.5 rounded-sm" />
                    <BaseButton type="submit" :label="id ? 'update' : 'create'"
                        customClass="bg-blue-500 hover:bg-blue-500/90 px-2 py-0.5 rounded-sm" />
                </div>
            </form>
        </div>
    </div>
</template>