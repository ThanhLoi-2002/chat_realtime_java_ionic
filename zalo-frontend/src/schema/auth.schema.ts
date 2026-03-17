import { useTranslate } from "@/composables/useTranslate"
import * as yup from "yup"

const { t } = useTranslate()

export const loginSchema = yup.object({
    phone: yup
        .string()
        .required(t("required")),

    password: yup
        .string()
        .required(t("required")),
})

export type LoginFormType = yup.InferType<typeof loginSchema>

export const registerSchema = yup.object({
    phone: yup
        .string()
        .required(t("required")),

    password: yup
        .string()
        .required(t("required")),

    confirmPassword: yup
        .string()
        .required(t("required"))
        .oneOf([yup.ref("password")], t("password_not_match")),

    username: yup
        .string()
        .required(t("required")),
})

export type RegisterFormType = yup.InferType<typeof registerSchema>