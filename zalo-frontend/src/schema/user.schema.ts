import { useTranslate } from "@/composables/useTranslate"
import * as yup from "yup"

const { t } = useTranslate()

export const userSchema = yup.object({
    username: yup
        .string()
        .required(t("required")),
})

export type UserFormType = yup.InferType<typeof userSchema>