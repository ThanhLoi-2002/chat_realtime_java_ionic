<script setup lang="ts">
import { oaStyle } from "@/assets/tailwindcss";
import BackButton from "@/components/Shared/Button/BackButton.vue";
import { useTranslate } from "@/composables/useTranslate";
import { LangFormType, langSchema } from "@/schema/lang.schema";
import { useLangStore } from "@/stores/Admin/lang.storage";
import { LANG_LABELS } from "@/utils/constant";
import { useForm } from "vee-validate";
import { useRouter } from "vue-router";

type LangKey = keyof LangFormType
const fields: LangKey[] = ["code", "vi", "en", "tw", "cn"]
const { t } = useTranslate()

const langStore = useLangStore()
const router = useRouter()
const { values, errors, handleSubmit, setFieldValue } = useForm<LangFormType>({
    validationSchema: langSchema,
    initialValues: {
        code: "",
        vi: "",
        en: "",
        tw: "",
        cn: ""
    },
    validateOnMount: false
})

const labels: Record<string, string> = LANG_LABELS

const onSubmit = handleSubmit((values) => {
    langStore.add(values, () => router.push("/admin/system/lang"))
},
    (errors) => {
        console.log("submit fail", errors)
    })

</script>

<template>
    <div :class="[oaStyle.bg.primary, 'min-h-screen']">
        <back-button />
        <div :class="[oaStyle.bg.primary, oaStyle.border.primary, 'border rounded-lg shadow p-6']">

            <form @submit.prevent="onSubmit">
                <div class="grid grid-cols-[200px_1fr] gap-x-6 gap-y-4 items-start">
                    <template v-for="key in fields" :key="key">

                        <label class="dark:text-white">{{ labels[key] }}</label>

                        <div>
                            <textarea :value="values[key]"
                                @input="setFieldValue(key, ($event.target as HTMLTextAreaElement).value)" rows="1"
                                :class="[oaStyle.bg.primary, oaStyle.border.primary, oaStyle.text.primary,
                                    `w-full px-3 py-2 rounded-md border
                                        focus:outline-none focus:ring-1 focus:ring-blue-400
                                        resize - y`]" />

                            <p v-if="errors[key]" class="text-red-500 text-xs">
                                {{ errors[key] }}
                            </p>
                        </div>

                    </template>

                </div>
                <div class="mt-6">
                    <ion-button type="submit" class="px-5 py-2 rounded-lg text-white">
                        💾 {{ t("save") }}
                    </ion-button>
                </div>
            </form>
        </div>

    </div>
</template>