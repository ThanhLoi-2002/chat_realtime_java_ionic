<script setup lang="ts">
import FormInput from "@/components/Input/FormInput.vue"
import LoadingSpinner from "@/components/Loading/LoadingSpinner.vue"
import { useTranslate } from "@/composables/useTranslate"
import { LoginFormType, loginSchema } from "@/schema/auth.schema"
import { useAuthStore } from "@/stores/auth"
import { ROUTE } from "@/utils/constant"
import { useForm } from "vee-validate"

const authStorage = useAuthStore()
const { t } = useTranslate()

const { values, errors, handleSubmit, setFieldValue } = useForm<LoginFormType>({
    validationSchema: loginSchema,
    initialValues: {
        phone: "",
        password: "",
    },
    validateOnMount: false
})

const onSubmit = handleSubmit((values) => {
    authStorage.login(values)
})
</script>

<template>
    <div class="w-full max-w-md">
        <!-- TITLE -->
        <h2 class="text-3xl font-bold mb-2 text-gray-900 dark:text-white">
            Welcome Back
        </h2>
        <p class="text-gray-500 dark:text-gray-400 mb-8">
            Enter your credentials to access your account
        </p>
        <form @submit.prevent="onSubmit">
            <!-- PHONE -->
            <FormInput label="Phone Number" :required="true" placeholder="Enter phone number" name="phone"
                v-model="values['phone']" :setFieldValue="setFieldValue" :error="errors['phone']" type="tel"
                prefix-icon="fa fa-phone" />

            <!-- PASSWORD -->
            <FormInput label="Password" :required="true" placeholder="Enter password" name="password"
                v-model="values['password']" :setFieldValue="setFieldValue" :error="errors['password']" type="password"
                prefix-icon="fa fa-lock" />

            <!-- LOGIN -->
            <button type="submit" class="w-full text-center cursor-pointer bg-primary text-white font-bold py-4 rounded-xl shadow-lg bg-blue-700 dark:border-blue-700
             hover:opacity-90 transition" :disabled="authStorage.isLoading">
                <LoadingSpinner v-if="authStorage.isLoading" />

                <span v-else>{{ t("login") }}</span>
            </button>
        </form>
        <!-- DIVIDER -->

        <div class="flex items-center my-8">
            <div class="flex-1 border-t border-gray-300 dark:border-gray-600"></div>
            <span class="px-4 text-sm text-gray-400 dark:text-gray-500">
                OR
            </span>
            <div class="flex-1 border-t border-gray-300 dark:border-gray-600"></div>
        </div>

        <!-- SOCIAL -->

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="flex items-center justify-center gap-2 border
               border-gray-300 dark:border-gray-600
               rounded-xl cursor-pointer
               bg-white dark:bg-gray-700
               text-gray-700 dark:text-gray-100
               hover:bg-gray-100 dark:hover:bg-gray-600 transition">
                <i class="fa-brands fa-google-plus-g py-3"></i>
                Google
            </div>

            <div class="flex items-center justify-center gap-2 border
               border-gray-300 dark:border-gray-600
               rounded-xl cursor-pointer
               bg-white dark:bg-gray-700
               text-gray-700 dark:text-gray-100
               hover:bg-gray-100 dark:hover:bg-gray-600 transition">
                <i class="fa-brands fa-facebook text-blue-600 py-3"></i>
                Facebook
            </div>

        </div>

        <p class="text-center text-gray-500 dark:text-gray-400 mt-8">
            Don't have an account?
            <router-link :to="ROUTE.REGISTER" class="">
                <span class="text-primary font-bold cursor-pointer">Sign up</span>
            </router-link>
        </p>

    </div>
</template>