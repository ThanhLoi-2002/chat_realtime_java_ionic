import * as yup from "yup"

export const oaSchema = yup.object({
    category: yup
        .string()
        .required("required"),
    categoryName: yup
        .string()
        .required("required"),
    name: yup
        .string()
        .required("required"),
    description: yup
        .string()
        .optional(),
        // .required("required"),
    province: yup
        .number()
        .required("required"),
    district: yup
        .number()
        .required("required"),
    address: yup
        .string()
        .required("required"),
    avatar: yup
        .string()
        .required("required"),
    cover: yup
        .string()
        .required("required")
})

export type OaFormType = yup.InferType<typeof oaSchema>