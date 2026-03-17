import * as yup from "yup"

export const langSchema = yup.object({
    code: yup
        .string()
        .required("Required"),

    vi: yup
        .string()
        .nullable(),

    en: yup
        .string()
        .nullable(),

    tw: yup
        .string()
        .nullable(),

    cn: yup
        .string()
        .nullable(),
})

export type LangFormType = yup.InferType<typeof langSchema>