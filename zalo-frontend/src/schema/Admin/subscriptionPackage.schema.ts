import * as yup from "yup"

export const subscriptionPackageSchema = yup.object({
    name: yup
        .string()
        .required("required"),
    description: yup
        .string()
        .optional(),
    price: yup
        .number()
        .required("required"),
    category: yup
        .string()
        .required("required"),
    targetType: yup
        .string()
        .required("required"),
    billingCycle: yup
        .string()
        .required("required"),
    level: yup
        .string()
        .required("required"),

    // monthlyPrice: yup
    //     .number().required(),
    savingsPercentage: yup
        .number().optional(),
    savingsAmount: yup
        .number().optional(),
    compareId: yup.number()
        .nullable().optional(),
    features: yup.array().of(
        yup.object({
            text: yup.string().required(),
            tag: yup.string().optional().nullable(),
            subtext: yup.string().optional().nullable()
        })
    ).optional(),

    gifts: yup.array().of(
        yup.object({
            title: yup.string().required("Vui lòng nhập tên quà tặng"),
            note: yup.string().optional().nullable()
        })
    ).optional()
})

export type SubscriptionPackageFormType = yup.InferType<typeof subscriptionPackageSchema>