<template>
    <img :class="[
        customClass,
        size ? size : 'size-12',
        'rounded-full',
        'cursor-pointer'
    ]" :src="url || RANDOM_AVATAR" @click="onClick" :id="id" />
</template>
<script setup lang="ts">
import { RANDOM_AVATAR } from '@/utils/constant';

defineProps<{
    url?: string
    size?: string
    onClick?: () => void
    id?: string,
    customClass?: string
}>()
</script>