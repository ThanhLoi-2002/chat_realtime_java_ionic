<script setup lang="ts">
import FormInput from "@/components/Input/FormInput.vue"
import LoadingSpinner from "@/components/Loading/LoadingSpinner.vue"
import { RegisterFormType, registerSchema } from "@/schema/auth.schema"
import { useAuthStore } from "@/stores/auth"
import { ROUTE } from "@/utils/constant"
import { useForm } from "vee-validate"

const authStorage = useAuthStore()

const { values, errors, handleSubmit, setFieldValue } = useForm<RegisterFormType>({
    validationSchema: registerSchema,
    initialValues: {
        phone: "",
        password: "",
        confirmPassword: "",
        username: "",
    },
    validateOnMount: false
})

const onSubmit = handleSubmit((values) => {
    authStorage.register(values)
})
</script>

<template>
    <div class="w-full max-w-md">
        <!-- TITLE -->
        <h2 class="text-3xl font-bold mb-2 text-gray-900 dark:text-white">
            Welcome Back
        </h2>
        <p class="text-gray-500 dark:text-gray-400 mb-8">
            Register your account
        </p>
        <form @submit.prevent="onSubmit">
            <!-- USERNAME -->
            <FormInput label="Username" :required="true" placeholder="Enter user name" name="username"
                v-model="values['username']" :error="errors['username']" :setFieldValue="setFieldValue" type="tel"
                prefix-icon="fa fa-user" />

            <!-- PHONE -->
            <FormInput label="Phone Number" :required="true" placeholder="Enter phone number" name="phone"
                v-model="values['phone']" :error="errors['phone']" :setFieldValue="setFieldValue" type="tel"
                prefix-icon="fa fa-phone" />

            <!-- PASSWORD -->
            <FormInput label="Password" :required="true" placeholder="Enter password" name="password"
                v-model="values['password']" :error="errors['password']" :setFieldValue="setFieldValue" type="password"
                prefix-icon="fa fa-lock" />

            <!-- CONFIRM PASSWORD -->
            <FormInput label="Confirm Password" :required="true" placeholder="Enter confirm password"
                name="confirmPassword" v-model="values['confirmPassword']" :error="errors['confirmPassword']"
                :setFieldValue="setFieldValue" type="password" prefix-icon="fa fa-lock" />

            <!-- REGISTER -->
            <button type="submit" class="w-full text-center cursor-pointer bg-primary text-white font-bold py-4 rounded-xl shadow-lg bg-blue-700 dark:border-blue-700
             hover:opacity-90 transition">
                <LoadingSpinner v-if="authStorage.isLoading" />

                <span v-else>Register</span>
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
            You have an account?
            <router-link :to="ROUTE.LOGIN" class="">
                <span class="text-primary font-bold cursor-pointer">Login</span>
            </router-link>
        </p>

    </div>
</template>