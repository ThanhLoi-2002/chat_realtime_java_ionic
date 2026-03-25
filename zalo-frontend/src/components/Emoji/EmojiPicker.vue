<script setup>
import { onMounted, ref } from 'vue'
import data from '@emoji-mart/data'
import { Picker } from 'emoji-mart'

const pickerRef = ref(null)
const emit = defineEmits(['select'])

onMounted(() => {
  const picker = new Picker({
    data,
    onEmojiSelect: (emoji) => {
      console.log(emoji.native)
      emit('select', emoji.native)
    }
  })

  pickerRef.value.appendChild(picker)
})
</script>

<template>
  <div ref="pickerRef"></div>
</template>