<template>
    <div class="relative" ref="dropdownRef">
        <!-- Nút kích hoạt dropdown (Hiển thị OA hiện tại) -->
        <div @click="isOpen = !isOpen"
            :class="[oaStyle.border.secondary, oaStyle.bg.hover, 'flex items-center space-x-2 px-3 py-1.5 cursor-pointer border-l border-r transition-colors select-none hover:rounded-md']">
            <circle-avatar :src="`${MINIO_URL}/${oaStor.oa?.avatar}`" size="size-7" />
            <div class="hidden sm:flex flex-col text-left max-w-30">
                <span class="text-sm font-medium truncate">{{ oaStor.oa?.name }}</span>
            </div>
            <i
                :class="['fas fa-chevron-down text-xs text-gray-500 transition-transform duration-200 ml-1', { 'rotate-180': isOpen }]" />
        </div>

        <!-- Menu danh sách OA xổ xuống -->
        <transition enter-active-class="transition duration-150 ease-out"
            enter-from-class="transform scale-95 opacity-0" enter-to-class="transform scale-100 opacity-100"
            leave-active-class="transition duration-100 ease-in" leave-from-class="transform scale-100 opacity-100"
            leave-to-class="transform scale-95 opacity-0">
            <div v-if="isOpen"
                :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'absolute left-0 sm:left-auto sm:right-0 mt-2 w-64 border rounded-xl shadow-xl py-2 z-50 overflow-hidden']">
                <div :class="[oaStyle.text.secondary, 'px-3 py-1.5 text-xs font-medium tracking-wider']">
                    Chuyển tài khoản OA
                </div>

                <!-- Danh sách các OA -->
                <div class="max-h-60 overflow-y-auto">
                    <div v-for="oa in oaStor.oas" :key="oa.id" @click="!actived(oa.id) && selectOa(oa)" :class="[
                        'flex items-center justify-between px-3 py-2.5 cursor-pointer transition-colors',
                        actived(oa.id) ? 'bg-slate-200 dark:bg-gray-700/50' : oaStyle.bg.hover
                    ]">
                        <div class="flex items-center space-x-2.5 min-w-0">
                            <circle-avatar :src="oa.avatar" size="size-8" class="shrink-0" />
                            <div class="flex flex-col min-w-0">
                                <span
                                    :class="['text-sm truncate', actived(oa.id) ? 'font-bold text-blue-600' : 'font-medium text-gray-700']">
                                    {{ oa.name }}
                                </span>
                            </div>
                        </div>

                        <!-- Dấu tích chọn OA hiện tại -->
                        <i v-if="actived(oa.id)" class="fas fa-check text-blue-600 text-xs ml-2 shrink-0" />
                    </div>
                </div>

                <!-- Footer hành động phụ (Tùy chọn) -->
                <div :class="[oaStyle.border.secondary, 'border-t mt-1 pt-1']">
                    <button @click="$router.push(OA_ROUTE.accounts)"
                        :class="[oaStyle.bg.hover, 'w-full text-left px-3 py-2 text-xs font-medium text-blue-600 transition-colors flex items-center space-x-2']">
                        <i class="fas fa-plus text-[10px]" />
                        <span>Danh sách tài khoản OA</span>
                    </button>
                </div>
            </div>
        </transition>
    </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss'
import { useOaStore } from '@/stores/Oa/oa.storage'
import { OaType } from '@/types/entities'
import { MINIO_URL, OA_ID, OA_ROUTE } from '@/utils/constant'
import { setKey } from '@/utils/local'
import { ref, onMounted, onUnmounted } from 'vue'

const oaStor = useOaStore()
// State quản lý
const isOpen = ref<boolean>(false)
const dropdownRef = ref<HTMLElement | null>(null)

const actived = (id: number) => {
    return oaStor.oa?.id === id
}

// Hàm chọn OA
const selectOa = (oa: OaType) => {
    setKey(OA_ID, oa.id)
    window.location.reload()
    // Thực hiện logic gọi API đổi context/token tại đây nếu cần
}

// Xử lý click ra ngoài để đóng dropdown
const handleClickOutside = (event: MouseEvent) => {
    if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
        isOpen.value = false
    }
}

onMounted(() => {
    document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside)
})
</script>