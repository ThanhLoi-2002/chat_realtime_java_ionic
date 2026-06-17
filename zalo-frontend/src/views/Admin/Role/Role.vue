<script setup lang="ts">
import axios from '@/api/axios'
import { oaStyle } from '@/assets/tailwindcss'
import PaginationTable from '@/components/Shared/Table/PaginationTable.vue'
import { useConfirmStore } from '@/composables/useConfirm'
import { useTranslate } from '@/composables/useTranslate'
import { useAdminRoleStore } from '@/stores/Admin/role.storage'
import { RoleType } from '@/types/entities'
import { IonButton } from '@ionic/vue'
import { ref, reactive, computed, onMounted, h, watch } from 'vue'

const { t } = useTranslate()
const roleStor = useAdminRoleStore()
const confirmStore = useConfirmStore();

// form dùng chung cho cả create và update
const form = reactive<Omit<RoleType, 'id'>>({
    name: '',
    description: '',
})

const editingId = ref<number | null>(null) // null = đang ở mode create
const isEditing = computed(() => editingId.value !== null)
const submitting = ref(false)
const formError = ref('')

const resetForm = () => {
    form.name = ''
    form.description = ''
    editingId.value = null
    formError.value = ''
}

const startEdit = (role: RoleType) => {
    editingId.value = role.id
    form.name = role.name
    form.description = role.description
    formError.value = ''
}

const submitForm = async () => {
    submitting.value = true

    let isSuccess
    if (isEditing.value && editingId.value !== null) {
        isSuccess = await roleStor.update(form, editingId.value)
    } else {
        isSuccess = await roleStor.add(form)
    }
    isSuccess && resetForm()
    submitting.value = false
}

onMounted(async () => await roleStor.getList())

const columns = [
    {
        id: "stt",
        header: t("orderNum"),
        trClass: "text-nowrap",
        cell: ({ row }: any) => row.index + 1,
    },
    {
        accessorKey: "name",
        header: t('name'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue())
    },
    {
        accessorKey: "description",
        header: t('description'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue())
    },
    {
        id: "action",
        header: t('action'),
        cell: ({ row }: any) => {
            const data = row.original

            return h("div", { class: "flex justify-center gap-2" }, [
                h(
                    IonButton,
                    {
                        size: "small",
                        color: "primary",
                        onClick: () => startEdit(data)
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
]

const openModal = (item: RoleType) => {
    confirmStore.open({
        title: t('delete'),
        message: `${t('areYouSureDelete')} ${item.name}?`,
        onOk: () => {
            roleStor.delete(item.id)
        }
    });
}
</script>

<template>
    <div :class="[oaStyle.bg.primary, 'mx-auto p-2']">
        <!-- Header -->
        <div class="mb-3">
            <div :class="[oaStyle.text.primary, 'text-2xl font-semibold']">{{ t('role') }}</div>
            <p :class="[oaStyle.text.muted, 'mt-1 text-sm']">{{ t('roleManagement') }}</p>
        </div>

        <!-- Form Create/Update -->
        <div :class="[oaStyle.bg.primary, oaStyle.border.primary, 'mb-6 rounded-xl border py-2 px-4 shadow-sm']">
            <h4 :class="[oaStyle.text.primary, 'mb-4 text-sm font-medium']">
                {{ isEditing ? t('edit') : t('add') }}
            </h4>

            <form @submit.prevent="submitForm" class="space-y-4">
                <div class="grid gap-4 sm:grid-cols-2">
                    <div>
                        <label :class="[oaStyle.text.secondary, 'mb-1 block text-sm font-medium']">{{ t('name')
                        }}</label>
                        <input v-model="form.name" type="text" placeholder="ADMIN" required
                            :class="[oaStyle.border.primary, oaStyle.text.secondary]"
                            class="w-full rounded-lg border px-3 py-2 text-sm placeholder:text-slate-400 focus:outline-none focus:border-slate-500" />
                    </div>
                    <div>
                        <label :class="[oaStyle.text.secondary, 'mb-1 block text-sm font-medium']">{{ t('desciption')
                        }}</label>
                        <input v-model="form.description" type="text" :placeholder="t('desciption')"
                            :class="[oaStyle.border.primary, oaStyle.text.secondary]"
                            class="w-full rounded-lg border px-3 py-2 text-sm placeholder:text-slate-400 focus:outline-none focus:border-slate-500" />
                    </div>
                </div>

                <div class="flex items-center gap-2">
                    <button type="submit" :disabled="submitting" :class="[oaStyle.bg.active, oaStyle.bg.hoverActive]"
                        class="inline-flex cursor-pointer items-center rounded-md px-4 py-2 text-sm font-medium text-white transition disabled:cursor-not-allowed disabled:opacity-60">
                        {{ submitting ? t('loading') : isEditing ? t('update') : t('add') }}
                    </button>
                    <button v-if="isEditing" type="button" @click="resetForm"
                        class="inline-flex cursor-pointer items-center rounded-md px-4 py-2 text-sm font-medium text-white transition bg-red-500 hover:bg-red-600">
                        {{ t('cancel') }}
                    </button>
                </div>
            </form>
        </div>

        <PaginationTable :data="roleStor.roles" :columns="columns" :pageSize="20" height="max-h-[50vh]"/>
    </div>
</template>