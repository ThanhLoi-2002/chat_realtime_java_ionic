<template>
    <div class="my-3">
        <label class="text-sm font-semibold mb-2 block text-gray-700 dark:text-gray-300">
            {{ label }} <span class="text-red-400" v-if="required">*</span>
        </label>
        <div class="relative">
            <!-- PREFIX ICON -->
            <i v-if="prefixIcon" class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500"
                :class="prefixIcon" />
            <!-- INPUT -->
            <input :value="modelValue" @input="updateValue"
                :type="type === 'password' ? (showPassword ? 'text' : 'password') : type" :placeholder="placeholder"
                :min="min" :max="max" :minlength="minlength" :maxlength="maxlength" class="w-full pl-12 pr-12 py-4 rounded-xl border
            border-gray-300 dark:border-gray-600
            bg-white dark:bg-gray-700
            text-gray-900 dark:text-white
            placeholder-gray-400 dark:placeholder-gray-500
            focus:border-blue-400
            outline-none transition" />

            <!-- PASSWORD TOGGLE -->
            <div v-if="type === 'password'" @click="togglePassword"
                class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500 cursor-pointer">
                <i class="fa fa-eye" v-if="showPassword"></i>
                <i class="fa-solid fa-eye-slash" v-else></i>
            </div>
        </div>
        <p v-if="error" class="text-red-500 text-xs mt-2">
            {{ error }}
        </p>
    </div>
</template>
<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps<{
    label: string;
    name: string;
    type?: string;
    required?: boolean;
    modelValue: string;
    placeholder?: string;

    min?: number | string;
    max?: number | string;
    minlength?: number;
    maxlength?: number;

    prefixIcon?: string;
    suffixIcon?: string;

    error?: string
    setFieldValue?: any
}>();

const showPassword = ref(false)

const togglePassword = () => {
    showPassword.value = !showPassword.value
}

const emit = defineEmits(["update:modelValue"])

const updateValue = (e: Event) => {
    props.setFieldValue(props.name, (e.target as HTMLInputElement).value)
}
</script>
