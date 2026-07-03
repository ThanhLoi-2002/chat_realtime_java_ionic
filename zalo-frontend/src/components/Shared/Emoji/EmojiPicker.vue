<script setup lang="ts">
import { onMounted, ref } from 'vue'
import data from '@emoji-mart/data'
import { Picker } from 'emoji-mart'
import { oaStyle } from '@/assets/tailwindcss'

const pickerRef = ref<HTMLElement | null>(null)
const emit = defineEmits(['select'])

onMounted(() => {
  const picker = new Picker({
    data,
    onEmojiSelect: (emoji: any) => {
      console.log(emoji.native)
      emit('select', emoji.native)
    },
    perLine: 7,          // Giảm số lượng emoji trên 1 hàng (mặc định là 9) giúp picker hẹp lại
    emojiSize: 18,       // Thu nhỏ kích thước của từng emoji (mặc định là 24)
    maxFrequentRows: 1,  // Giới hạn hàng chứa các emoji hay dùng để giảm chiều cao
  })

  pickerRef.value?.appendChild(picker as any)
})
</script>

<template>
  <div ref="pickerRef" :class="[oaStyle.bg.secondary, 'custom-mini-picker']"></div>
</template>

<style scoped>
.custom-mini-picker :deep(em-emoji-picker) {
  /* Ép chiều rộng và chiều cao tối đa cho toàn bộ component */
  width: 100% !important;
  height: 330px !important;

  /* Hạ size chữ của các phần danh mục, ô tìm kiếm bên trong Shadow DOM */
  --em-id: mini-picker;
  --font-size: 13px;
}
</style>