<template>
    <div class="flex flex-col items-center justify-center h-full gap-4">
      
      <!-- QR -->
      <canvas ref="canvas"></canvas>

    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import QRCode from 'qrcode'
import { qrCodeUrl } from '@/utils/constant'

const props = defineProps<{
    code: string
}>()

const canvas = ref<HTMLCanvasElement | null>(null)

const generateQR = () => {
  if (!canvas.value) return

  const value = `${qrCodeUrl}/${props.code}`

  QRCode.toCanvas(canvas.value, value, {
    width: 220,
    margin: 2
  })
}

onMounted(generateQR)
// watch(inviteCode, generateQR)
</script>