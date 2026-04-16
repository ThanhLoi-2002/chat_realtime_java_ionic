<template>
  <div class="w-[97%] overflow-x-auto">
    <div v-if="files.length > 0" 
         class="flex flex-nowrap gap-3 p-3 w-1/2 border-t border-gray-200 dark:border-slate-700 bg-gray-50/50 dark:bg-gray-900/50 scrollbar-hide">
      
      <div v-for="(file, index) in files" :key="index"
           class="relative w-20 h-20 shrink-0 border border-gray-200 dark:border-slate-700 rounded-lg bg-white dark:bg-gray-800 flex flex-col items-center justify-center overflow-hidden">
        
        <template v-if="file.type === ResourceEnum.IMAGE || file.type === ResourceEnum.VIDEO">
          <img v-if="file.type === ResourceEnum.IMAGE" :src="file.url" class="w-full h-full object-cover" />
          <video v-else :src="file.url" class="w-full h-full object-cover" />
        </template>

        <button @click="$emit('remove', index)" class="absolute top-0.5 right-0.5 bg-black/50 text-white rounded-full w-4 h-4 text-[8px]">✕</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ResourceEnum } from '@/types/enum';
defineProps<{ files: any[] }>();
defineEmits(['remove']);
</script>