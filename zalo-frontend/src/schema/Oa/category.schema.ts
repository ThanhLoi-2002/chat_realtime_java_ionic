import * as yup from "yup"

export const categorySchema = yup.object({
    name: yup
        .string()
        .required("required"),
    description: yup
        .string()
        .optional(),
    code: yup
        .string()
        .required("required"),
})

export type CategoryFormType = yup.InferType<typeof categorySchema>