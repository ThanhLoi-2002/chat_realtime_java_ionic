<template>
    <label class="relative inline-flex items-center cursor-pointer">
        <!-- Input checkbox ẩn -->
        <input type="checkbox" :checked="modelValue" @change="toggle" class="sr-only peer">

        <!-- Track (Đường ray) -->
        <div class="w-7 h-4 bg-gray-300 peer-focus:outline-none rounded-full peer 
                dark:bg-gray-700 peer-checked:after:translate-x-full 
                peer-checked:after:border-white after:content-[''] 
                after:absolute after:top-0.5 after:left-0.5 
                after:bg-white after:border-gray-300 after:border 
                after:rounded-full after:h-3 after:w-3 after:transition-all 
                peer-checked:bg-blue-600">
        </div>

        <!-- Label đi kèm (nếu có) -->
        <span v-if="label" class="ml-3 text-xs dark:text-gray-400">
            {{ label }}
        </span>
    </label>
</template>

<script setup lang="ts">
// Định nghĩa props để nhận giá trị từ v-model
const props = defineProps<{
    modelValue: boolean;
    label?: string;
}>();

// Định nghĩa emits để cập nhật lại giá trị cho cha
const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void;
}>();

const toggle = (event: Event) => {
    const target = event.target as HTMLInputElement;
    emit('update:modelValue', target.checked);
};
</script>