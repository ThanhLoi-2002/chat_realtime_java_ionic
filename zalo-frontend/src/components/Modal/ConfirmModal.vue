<template>
    <ion-alert :is-open="showConfirm" :header="header ?? 'Delete item'" :message="message ?? 'Bạn có chắc muốn xoá item này?'"
        css-class="custom-alert" :buttons="alertButtons" />
</template>

<script setup lang="ts">

const props = defineProps<{
    showConfirm: boolean
    header?: string
    message?: string
    onOk: () => void
}>()

const emit = defineEmits(["update:showConfirm"])

const alertButtons = [
    {
        text: "Cancel",
        role: "cancel",
        cssClass: "cursor-pointer",
        handler: () => {
            emit("update:showConfirm", false)
        }
    },
    {
        text: "OK",
        cssClass: "cursor-pointer",
        handler: () => {
            props.onOk()
            emit("update:showConfirm", false)
        }
    }
]
</script>