<template>
    <div class="flex gap-2 border-b pb-1 border-gray-200 dark:border-slate-700">
        <button @click.stop="$emit('toggle-emoji')"
            class="text-sm md:text-xl text-gray-500 dark:text-gray-400 hover:text-blue-500 transition cursor-pointer">
            🙂
        </button>

        <input type="file" ref="fileInput" multiple accept="image/*,video/*" class="hidden"
            @change="(e) => $emit('select-media', e)" />
        <button @click="fileInput?.click()" class="text-sm md:text-xl text-gray-500 dark:text-gray-400 cursor-pointer">
            📎
        </button>

        <input type="file" ref="docInput" multiple accept=".doc,.docx,.xls,.xlsx,.ppt,.pptx,.pdf,.txt" class="hidden"
            @change="(e) => $emit('select-doc', e)" />
        <button @click="docInput?.click()"
            class="text-sm md:text-xl text-gray-500 dark:text-gray-400 hover:text-blue-500 cursor-pointer transition">
            <i class="fa-solid fa-file-lines"></i>
        </button>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
const fileInput = ref<HTMLInputElement | null>(null);
const docInput = ref<HTMLInputElement | null>(null);

defineEmits(['toggle-emoji', 'select-media', 'select-doc']);
</script>