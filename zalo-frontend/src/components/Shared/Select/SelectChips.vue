<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from "vue"
import { oaStyle } from "@/assets/tailwindcss"
import { SelectOptionType } from "@/types/common";

const props = withDefaults(defineProps<{
    modelValue: (string | number)[]; // v-model lúc này sẽ lưu mảng các ID [1, 2] hoặc ['ADMIN', 'USER']
    options: SelectOptionType[];         // Mảng object [{id: 1, name: 'ROLE_ADMIN'}]
    placeholder?: string;
    searchPlaceholder?: string;
}>(), {
    placeholder: "Select options...",
    searchPlaceholder: "Search..."
})

const emit = defineEmits<{
    (e: 'update:modelValue', value: (string | number)[]): void
}>()

const isOpen = ref(false)
const searchQuery = ref("")
const dropdownRef = ref<HTMLElement | null>(null)

const selectedIds = computed({
    get: () => props.modelValue ?? [],
    set: (val) => emit('update:modelValue', val)
})

// Hàm tiện ích để tìm nhanh Name từ ID nhằm hiển thị trên thanh Chip
const getNameById = (id: string | number) => {
    const found = props.options.find(opt => opt.value === id)
    return found ? found.label : id
}

// Tìm kiếm theo trường 'name' của Object
const filteredOptions = computed(() => {
    if (!searchQuery.value.trim()) return props.options
    return props.options.filter(option => 
        option.label.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
})

// Bật/Tắt chọn bằng ID
const toggleOption = (id: string | number) => {
    const current = [...selectedIds.value]
    const index = current.indexOf(id)
    if (index > -1) {
        current.splice(index, 1)
    } else {
        current.push(id)
    }
    selectedIds.value = current
}

const handleClickOutside = (event: MouseEvent) => {
    if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
        isOpen.value = false
        searchQuery.value = ""
    }
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onUnmounted(() => document.removeEventListener('click', handleClickOutside))
</script>

<template>
    <div class="w-full relative" ref="dropdownRef">
        <div 
            @click="isOpen = !isOpen"
            :class="[
                oaStyle.bg.primary, 
                oaStyle.border.primary, 
                oaStyle.text.primary,
                'w-full min-h-10.5 px-3 py-2 rounded-md border flex flex-wrap gap-1.5 items-center justify-between cursor-pointer select-none'
            ]"
        >
            <span v-if="selectedIds.length === 0" class="text-gray-400 dark:text-gray-500 text-sm">
                {{ props.placeholder }}
            </span>
            
            <div v-else class="flex flex-wrap gap-1">
                <span 
                    v-for="id in selectedIds" 
                    :key="id"
                    class="bg-blue-100 text-blue-800 dark:bg-blue-900/40 dark:text-blue-300 text-xs px-2 py-1 rounded flex items-center gap-1 font-medium"
                >
                    {{ getNameById(id) }}
                    <i class="fas fa-times cursor-pointer hover:text-blue-500" @click.stop="toggleOption(id)"></i>
                </span>
            </div>

            <i :class="['fas fa-chevron-down text-gray-400 text-xs transition-transform duration-200', isOpen ? 'rotate-180' : '']"></i>
        </div>

        <div 
            v-if="isOpen"
            :class="[
                oaStyle.bg.primary, 
                oaStyle.border.primary, 
                'absolute left-0 right-0 mt-1 border rounded-md shadow-lg z-50 flex flex-col overflow-hidden'
            ]"
        >
            <div class="p-2 border-b sticky top-0 z-10" :class="[oaStyle.border.primary, oaStyle.bg.primary]">
                <div class="relative flex items-center">
                    <i class="fas fa-search absolute left-3 text-gray-400 text-xs"></i>
                    <input 
                        v-model="searchQuery"
                        type="text"
                        :placeholder="props.searchPlaceholder"
                        :class="[
                            oaStyle.bg.primary,
                            oaStyle.text.primary,
                            'w-full pl-8 pr-3 py-1.5 text-sm rounded border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-1 focus:ring-blue-500'
                        ]"
                    />
                </div>
            </div>

            <div class="max-h-48 overflow-y-auto divide-y" :class="['divide-gray-100 dark:divide-gray-700/50']">
                <div v-if="filteredOptions.length === 0" class="px-4 py-3 text-sm text-gray-400 dark:text-gray-500 text-center select-none">
                    No results found
                </div>

                <div 
                    v-else
                    v-for="option in filteredOptions" 
                    :key="option.value"
                    @click="toggleOption(option.value)"
                    :class="[
                        selectedIds.includes(option.value) ? 'bg-gray-100 dark:bg-gray-700/70' : 'hover:bg-gray-50 dark:hover:bg-gray-700/30',
                        oaStyle.text.primary,
                        'px-4 py-2.5 flex items-center justify-between cursor-pointer text-sm transition-colors duration-150'
                    ]"
                >
                    <span>{{ option.label }}</span>
                    <i :class="[
                        selectedIds.includes(option.value) ? 'fas fa-check-square text-blue-500' : 'far fa-square text-gray-400'
                    ]"></i>
                </div>
            </div>
        </div>
    </div>
</template>