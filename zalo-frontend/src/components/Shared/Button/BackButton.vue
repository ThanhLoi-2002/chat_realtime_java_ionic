<template>
    <button :class="[oaStyle.text.primary, 'cursor-pointer text-xl size-10']" @click="$router.back()">
        <i class="fas fa-arrow-circle-left"></i>
    </button>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';

</script>