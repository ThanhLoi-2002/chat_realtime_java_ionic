<template>
    <ion-alert :is-open="showConfirm" :header="header ?? 'Delete item'"
        :message="message ?? 'Bạn có chắc muốn xoá item này?'"
        :css-class="['custom-alert', sysStorage.isDarkMode ? 'custom-alert--dark' : '']" :buttons="alertButtons" />
</template>

<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { useSystemStore } from '@/stores/system.storage';

const props = defineProps<{
    showConfirm: boolean
    header?: string
    message?: string
    onOk: () => void
}>()

const sysStorage = useSystemStore()
const { t } = useTranslate()
const emit = defineEmits(["update:showConfirm"])

const alertButtons = [
    {
        text: t("cancel"),
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

<style>
/* Light */
.custom-alert {
    --background: #ffffff;

    --ion-color-step-850: #0f172a;
    /* header */
    --ion-text-color: #0f172a;
}

/* Dark */
.custom-alert--dark {
    --background: #1e1e1e;

    --ion-color-step-850: #f9fafb;
    --ion-text-color: #e5e7eb;
}

/* Button */
/* .custom-alert .alert-button {
    color: var(--color);
} */

/* Header */
.custom-alert .alert-headline {
    color: var(--ion-color-step-850);
}

/* Message */
.custom-alert .alert-message {
    color: var(--ion-text-color);
}
</style>