<template>
    <ion-item class="rounded-lg border ">
        <ion-select v-model="lang" interface="alert" :class="customClass ? customClass : 'text-sm'">
            <ion-select-option value="vi">Tiếng Việt</ion-select-option>
            <ion-select-option value="en">English</ion-select-option>
            <ion-select-option value="cn">中文</ion-select-option>
            <ion-select-option value="tw">繁體中文</ion-select-option>
        </ion-select>

    </ion-item>
</template>

<script setup lang="ts">
import { useLangStore } from "@/stores/lang"
import { ref, watch } from "vue"

const props = defineProps<{
    customClass?: string
}>()

const langStore = useLangStore()
const lang = ref(langStore.lang)

watch(lang, (value) => {
    langStore.getListByLang(value)
})
</script>