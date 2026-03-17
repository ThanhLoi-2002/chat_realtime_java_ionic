<template>
  <div :class="[
      'fa-solid fa-spinner fa-spin text-gray-700 dark:text-white',
      customClass
    ]"
     :style="{ fontSize: size || '20px' }"></div>
</template>

<script setup lang="ts">
const props = defineProps<{
  size?: string; // Made size optional
  customClass?: string; // Made class optional
}>();
</script>

<style scoped>
.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}
</style>