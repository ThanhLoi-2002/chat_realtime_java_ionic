<template>
    <button :class="customClass" :type="type" :disabled="isLoading" @click="onClick">
        <span v-if="isLoading" class="flex align-items-center justify-content-center">
            <span class="mr-2 flex">
                <LoadingSpinner />
            </span>
            {{ loadingLabel || "Loading" }}
        </span>
        <span v-else class="flex gap-2">
            <i :class="icon" v-if="icon"/>
            {{ label }}
        </span>
    </button>
</template>

<script setup lang="ts">
import LoadingSpinner from '../Loading/LoadingSpinner.vue';

// Định nghĩa các props cho button
const props = defineProps<{
    icon?: string;
    label?: string;
    loadingLabel?: string;
    type?: "button" | "submit" | "reset" | undefined;
    isLoading?: boolean;
    disabled?: boolean;
    customClass?: string;
    onClick?: () => void
}>();
</script>