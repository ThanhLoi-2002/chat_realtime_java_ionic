<template>
    <img :class="[
        customClass,
        size ? size : 'size-12',
        'rounded-full',
        'cursor-pointer'
    ]" :src="url" @click="onClick" :id="id" />
</template>
<script setup lang="ts">
defineProps<{
    url?: string
    size?: string
    onClick?: () => void
    id?: string,
    customClass?: string
}>()
</script>