<script setup lang="ts">
import { useTranslate } from "@/composables/useTranslate";
import { LangFormType, langSchema } from "@/schema/lang.schema";
import { useLangStore } from "@/stores/lang.storage";
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
    langStore.add(values, () => router.push("/languages"))
},
    (errors) => {
        console.log("submit fail", errors)
    })

</script>

<template>
    <div class="p-6 bg-gray-100 dark:bg-gray-900 min-h-screen">

        <div class="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg shadow p-6">
            <form @submit.prevent="onSubmit">
                <div class="grid grid-cols-[200px_1fr] gap-x-6 gap-y-4 items-start">
                    <template v-for="key in fields" :key="key">

                        <label class="dark:text-white">{{ labels[key] }}</label>

                        <div>
                            <textarea :value="values[key]"
                                @input="setFieldValue(key, ($event.target as HTMLTextAreaElement).value)" rows="1"
                                class="w-full px-3 py-2 rounded-md
                                        border border-gray-300 dark:border-gray-600
                                        bg-white dark:bg-gray-900
                                        text-gray-800 dark:text-gray-200
                                        focus:outline-none
                                        focus:ring-2 focus:ring-blue-500
                                        resize-y" />

                            <p v-if="errors[key]" class="text-red-500 text-xs">
                                {{ errors[key] }}
                            </p>
                        </div>

                    </template>

                </div>
                <div class="mt-6">
                    <ion-button type="submit" class="px-5 py-2 rounded-lg text-white">
                        💾 {{t("save")}}
                    </ion-button>
                </div>
            </form>
        </div>

    </div>
</template>