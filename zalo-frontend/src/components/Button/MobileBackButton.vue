<template>
    <button class="md:hidden text-gray-600 dark:text-white" @click="onClick">
        <i class="fas fa-arrow-circle-left"></i>
    </button>
</template>
<script setup lang="ts">
const props = defineProps<{
    onClick: () => void
}>()
</script>