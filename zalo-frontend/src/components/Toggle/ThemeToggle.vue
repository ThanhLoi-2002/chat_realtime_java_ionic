<script setup lang="ts">
import { THEME } from '@/utils/constant'
import { getKey, setKey } from '@/utils/local'
import { ref } from 'vue'

const props = defineProps<{
  customClass?: string
}>()

const isDark = ref(getKey(THEME) === 'dark')

const toggleDark = () => {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.classList.add('dark')
    setKey(THEME, "dark")
  } else {
    document.documentElement.classList.remove('dark')
    setKey(THEME, "light")
  }
}
</script>

<template>
  <div :class="customClass ? customClass : 'p-4'"
    class=" hover:bg-gray-200 dark:hover:bg-gray-700 rounded-lg cursor-pointer text-yellow-400" @click="toggleDark">
    <i v-if="isDark" class="fa-solid fa-moon"></i>
    <i v-else class="fa-solid fa-sun"></i>
  </div>
</template>