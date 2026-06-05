<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import { computed } from 'vue';

// Định nghĩa model để binding dữ liệu 2 chiều với component cha
const model = defineModel<any>({ default: null });

const props = defineProps<{
    placeholder: string;
}>();

const formatDateInput = computed(() => {
    if (!model.value) return ''
    return `${model.value.getDate().toString().padStart(2, '0')}/${(model.value.getMonth() + 1).toString().padStart(2, '0')}/${model.value.getFullYear()}`
})

// Hàm xóa nhanh từ khóa khi bấm vào nút X
const clear = () => {
    model.value = null;
    console.log(model.value)
};
</script>

<template>
    <div class="relative flex items-center w-full">
        <input :value="formatDateInput" type="text" :placeholder="placeholder" class="w-full h-8 text-sm rounded-md pl-1.5 pr-6
                        placeholder-slate-400
                        dark:placeholder-slate-400/70
                       border
                       focus:outline-none focus:border-[#3b82f6]
                       transition-all duration-200"
            :class="[style.border.primary, style.text.secondary, style.bg.secondary]" disabled />

        <i v-if="model" type="button" @click="clear" class="fas fa-times-circle absolute flex items-center justify-center 
                        hover:text-slate-600 
                        dark:hover:text-slate-300 
                       transition-colors focus:outline-none z-10 cursor-pointer text-sm right-1.5"
            :class="[style.text.muted]">
        </i>

        <i v-else class="far fa-calendar-alt absolute flex items-center justify-center 
                       transition-colors focus:outline-none z-10 cursor-pointer text-sm right-1.5"
            :class="[style.text.muted]"></i>
    </div>
</template>