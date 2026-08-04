import * as yup from "yup"

export const oaCategorySchema = yup.object({
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

export type OaCategoryFormType = yup.InferType<typeof oaCategorySchema>