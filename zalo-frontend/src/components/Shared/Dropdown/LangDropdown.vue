<template>
    <div ref="dropdownRef" class="relative w-full text-sm select-none">

        <button @click="isOpen = !isOpen" :class="[
            oaStyle.bg.hover,
            isCollapsed ? 'justify-center px-0 h-9' : 'px-3 py-2 border rounded-lg justify-between h-10',
            'w-full flex items-center transition-all duration-300 cursor-pointer text-gray-700 dark:text-gray-300 border-slate-200 dark:border-slate-700'
        ]">
            <div class="flex items-center" :class="[isCollapsed ? '' : 'space-x-3 min-w-0']">
                <i
                    class="fa-solid fa-earth-americas text-base text-gray-500 dark:text-gray-400 shrink-0 text-center w-5" />
                <span :class="[
                    'whitespace-nowrap transition-all duration-300 ease-in-out font-medium text-left',
                    isCollapsed ? 'opacity-0 max-w-0 pointer-events-none' : 'opacity-100 max-w-30'
                ]">
                    {{ currentLangLabel }}
                </span>
            </div>

            <i :class="[
                isOpen ? 'fa-solid fa-chevron-up' : 'fa-solid fa-chevron-down',
                'text-xs text-gray-400 transition-all duration-300 shrink-0',
                isCollapsed ? 'opacity-0 max-w-0 scale-0' : 'opacity-100'
            ]" />
        </button>

        <transition name="fade">
            <div v-show="isOpen && !isCollapsed"
                class="absolute top-full left-0 mt-1 w-full bg-white dark:bg-gray-800 border border-slate-200 dark:border-slate-700 rounded-lg shadow-xl z-999 overflow-hidden py-1">
                <button v-for="option in langOptions" :key="option.value" @click="selectLang(option.value)" :class="[
                    lang === option.value
                        ? 'bg-blue-50 text-blue-600 dark:bg-blue-950/50 dark:text-blue-400 font-semibold'
                        : 'text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700/50',
                    'w-full text-left px-4 py-2 text-sm transition-colors block cursor-pointer whitespace-nowrap'
                ]">
                    {{ option.label }}
                </button>
            </div>
        </transition>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useLangStore } from '@/stores/App/lang.storage';
import { oaStyle } from '@/assets/tailwindcss';

// Nhận prop đóng mở từ Sidebar cha
interface Props {
    isCollapsed?: boolean;
}
withDefaults(defineProps<Props>(), {
    isCollapsed: false,
});

const langStore = useLangStore();
const lang = ref(langStore.lang);

const isOpen = ref(false);
const dropdownRef = ref<HTMLElement | null>(null);

// Danh sách ngôn ngữ cấu hình rõ ràng
const langOptions = [
    { value: 'vi', label: 'Tiếng Việt' },
    { value: 'en', label: 'English' },
    { value: 'cn', label: '中文' },
    { value: 'tw', label: '繁體中文' }
];

// Lấy Label tương ứng với mã lang hiện tại để hiển thị lên nút bấm
const currentLangLabel = computed(() => {
    const found = langOptions.find(opt => opt.value === lang.value);
    return found ? found.label : 'Tiếng Việt';
});

// Hàm click chọn ngôn ngữ
const selectLang = (val: string) => {
    lang.value = val;
    langStore.getListByLang(val);
    isOpen.value = false; // Chọn xong tự đóng box
};

// Xử lý Click Outside: Bấm ra ngoài vùng dropdown thì tự động đóng lại
const handleClickOutside = (event: MouseEvent) => {
    if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
        isOpen.value = false;
    }
};

onMounted(() => {
    window.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
    window.removeEventListener('click', handleClickOutside);
});
</script>