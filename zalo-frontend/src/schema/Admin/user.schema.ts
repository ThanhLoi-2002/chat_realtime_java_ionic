import * as yup from "yup"

export const userSchema = yup.object({
    username: yup
        .string()
        .required("required"),
    phone: yup
        .string()
        .required("required"),
})

export type UserFormType = yup.InferType<typeof userSchema>