<script setup lang="ts">
import { useConfirmStore } from "@/composables/useConfirm";
import { useTranslate } from "@/composables/useTranslate";
import { IonButton } from "@ionic/vue";
import { ref, onMounted, computed, h } from "vue";

const { t } = useTranslate()
const roles = ref([]);
const confirmStore = useConfirmStore();
const selectedRoles = ref<number[]>([]);

const columns = computed(() => [
    {
        id: "stt",
        header: () => t("orderNum"),
        trClass: "text-nowrap",
        cell: ({ row }: any) => row.index + 1,
    },
    {
        accessorKey: "name",
        header: () => t('name'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue())
    },
    {
        accessorKey: "description",
        header: () => t('description'),
        cell: ({ getValue }: any) =>
            h("span", { class: "whitespace-nowrap" }, getValue())
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
])

const openModal = (item: any) => {
    confirmStore.open({
        title: t('delete'),
        message: `${t('areYouSureDelete')} ${item.name}?`,
        onOk: () => {
            roleStor.delete(item.id)
        }
    });
}

onMounted(loadData);
</script>

<template>
  <div class="space-y-3">

    <label
      v-for="role in roles"
      :key="role.id"
      class="flex items-center gap-2"
    >
      <input
        type="checkbox"
        :value="role.id"
        v-model="selectedRoles"
      />

      {{ role.name }}
    </label>

    <button
      class="px-4 py-2 rounded bg-blue-500 text-white"
      @click="save"
    >
      Save
    </button>

  </div>
</template>