<template>
    <div ref="dropdownRef" class="relative w-full text-sm select-none">

        <!-- Nút bấm mở dropdown -->
        <button @click="toggleDropdown" :class="[
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
                'fa-solid fa-chevron-down text-xs text-gray-400 transition-all duration-300 shrink-0 ml-2',
                isCollapsed ? 'opacity-0 max-w-0 scale-0' : 'opacity-100',
                { 'rotate-180': isOpen }
            ]" />
        </button>

        <!-- Teleport ra body để thoát khỏi mọi giới hạn sticky/overflow -->
        <Teleport to="body">
            <transition name="fade">
                <div v-if="isOpen && !isCollapsed" :style="{
                    top: dropdownPos.top,
                    left: dropdownPos.left,
                    width: dropdownPos.width
                }"
                    class="fixed bg-white dark:bg-gray-800 border border-slate-200 dark:border-slate-700 rounded-lg shadow-xl overflow-hidden py-1">
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
        </Teleport>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, nextTick, onMounted, onUnmounted } from 'vue';
import { useLangStore } from '@/stores/Admin/lang.storage';
import { oaStyle } from '@/assets/tailwindcss';

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

// Quản lý tọa độ vị trí hiển thị khi Teleport
const dropdownPos = reactive({
    top: '0px',
    left: '0px',
    width: '0px'
});

const langOptions = [
    { value: 'vi', label: 'Tiếng Việt' },
    { value: 'en', label: 'English' },
    { value: 'cn', label: '中文' },
    { value: 'tw', label: '繁體中文' }
];

const currentLangLabel = computed(() => {
    const found = langOptions.find(opt => opt.value === lang.value);
    return found ? found.label : 'Tiếng Việt';
});

// Cập nhật vị trí chính xác dựa theo nút bấm
const updatePosition = () => {
    if (dropdownRef.value) {
        const rect = dropdownRef.value.getBoundingClientRect();
        dropdownPos.top = `${rect.bottom + 4}px`; // cách nút bấm 4px
        dropdownPos.left = `${rect.left}px`;
        dropdownPos.width = `${rect.width}px`;
    }
};

const toggleDropdown = async () => {
    isOpen.value = !isOpen.value;
    if (isOpen.value) {
        await nextTick();
        updatePosition();
    }
};

const selectLang = (val: string) => {
    lang.value = val;
    langStore.getListByLang(val);
    isOpen.value = false;
};

// Đóng dropdown khi click ra ngoài hoặc khi scroll/resize trang
const handleClickOutside = (event: MouseEvent) => {
    if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
        isOpen.value = false;
    }
};

const handleScrollOrResize = () => {
    if (isOpen.value) {
        updatePosition();
    }
};

onMounted(() => {
    window.addEventListener('click', handleClickOutside);
    window.addEventListener('scroll', handleScrollOrResize, true);
    window.addEventListener('resize', handleScrollOrResize);
});

onUnmounted(() => {
    window.removeEventListener('click', handleClickOutside);
    window.removeEventListener('scroll', handleScrollOrResize, true);
    window.removeEventListener('resize', handleScrollOrResize);
});
</script>