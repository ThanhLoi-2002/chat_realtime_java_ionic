<template>
  <ion-content>
    <div class="relative w-full h-full bg-black">

      <!-- Camera -->
      <video ref="video" class="w-full h-full object-cover"></video>

      <!-- Overlay -->
      <div class="absolute inset-0 flex items-center justify-center">
        <div class="w-64 h-64 border-4 border-white rounded-xl"></div>
      </div>

      <!-- Bottom -->
      <div class="absolute bottom-10 w-full flex justify-center gap-3">
        <input type="file" accept="image/*" @change="scanImage" />
      </div>

    </div>
  </ion-content>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { BrowserMultiFormatReader } from '@zxing/browser'
import { useRouter } from 'vue-router'

const video = ref<HTMLVideoElement | null>(null)
const reader = new BrowserMultiFormatReader()
const router = useRouter()

const start = async () => {
  const devices = await BrowserMultiFormatReader.listVideoInputDevices()

  const backCam = devices.find(d =>
    d.label.toLowerCase().includes('back')
  ) || devices[0]

  if (backCam) {
    reader.decodeFromVideoDevice(backCam.deviceId, video.value!, (res) => {
      if (res) {
        handleResult(res.getText())
      }
    })
  }
}

const stop = () => {
  (reader as any).reset()
}

const handleResult = (text: string) => {
  stop()

  const match = text.match(/join\/(.+)$/)
  if (match) {
    const code = match[1]
    router.push(`/g/${code}`)
  }
}

const scanImage = async (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return

  const img = new Image()
  img.src = URL.createObjectURL(file)

  img.onload = async () => {
    const res = await reader.decodeFromImageElement(img)
    handleResult(res.getText())
  }
}

onMounted(start)
onUnmounted(stop)
</script>