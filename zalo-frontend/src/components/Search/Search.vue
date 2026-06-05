<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import { RoundedStyle, TextStyle } from '@/types/style';

// Định nghĩa model để binding dữ liệu 2 chiều với component cha
const model = defineModel<string>({ default: '' });

const props = defineProps<{
    placeholder: string;
    rounded: RoundedStyle
    height: string
    textSize: TextStyle
    iconRight: string
    iconLeft: string
    pxContent: string
}>();

// Hàm xóa nhanh từ khóa khi bấm vào nút X
const clearSearch = () => {
    model.value = '';
};
</script>

<template>
    <div class="relative flex items-center w-full">
        <i class="fas fa-search absolute text-slate-400 dark:text-slate-500 pointer-events-none z-10"
            :class="[textSize, iconLeft]"></i>

        <input v-model="model" type="text" :placeholder="placeholder" class="w-full text-sm
                        placeholder-slate-400
                        dark:placeholder-slate-400/70
                       border
                       focus:outline-none focus:border-[#3b82f6]
                       transition-all duration-200"
            :class="[rounded, style.border.primary, style.text.secondary, style.bg.secondary, height, pxContent, textSize]" />

        <i v-if="model && model.length > 0" type="button" @click="clearSearch" class="fas fa-times-circle absolute flex items-center justify-center 
                        hover:text-slate-600 
                        dark:hover:text-slate-300 
                       transition-colors focus:outline-none z-10 cursor-pointer"
            :class="[style.text.muted, textSize, iconRight]">
        </i>
    </div>
</template>