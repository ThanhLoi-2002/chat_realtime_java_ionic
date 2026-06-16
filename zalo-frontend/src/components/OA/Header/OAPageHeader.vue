<template>
    <div :class="[
        oaStyle.bg.primary,
        oaStyle.border.primary,
        'h-10 border-b flex items-center justify-between text-sm shrink-0 w-full select-none'
    ]">
        <div class="flex items-center space-x-3 min-w-0">
            <button @click="sysStorage.toggleSidebar()"
                :class="[oaStyle.bg.hover, oaStyle.text.secondary, 'p-1.5 rounded cursor-pointer flex items-center justify-center']">
                <i class="fa-solid fa-bars text-sm" />
            </button>

            <nav :class="[oaStyle.text.secondary, 'flex items-center space-x-2 overflow-hidden whitespace-nowrap']">
                <div v-for="(crumb, index) in breadcrumbs" :key="index" class="flex items-center">
                    <span v-if="index > 0" class="mx-2 text-gray-300 dark:text-gray-600">/</span>

                    <span :class="[
                        index === breadcrumbs.length - 1
                            ? 'font-semibold text-slate-800 dark:text-slate-200'
                            : [oaStyle.text.hoverBlue, 'cursor-pointer transition-colors']
                    ]">
                        {{ crumb.name }}
                    </span>
                </div>
            </nav>
        </div>

        <div class="flex items-center space-x-2 shrink-0 pl-4">
            <slot name="actions" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import { useOASystemStore } from '@/stores/Oa/oaSystem.storage';

// Khai báo cấu trúc dữ liệu cho breadcrumb mang tính động
interface Breadcrumb {
    name: string;
    path?: string;
}

defineProps<{
    breadcrumbs: Breadcrumb[]
}>();

const sysStorage = useOASystemStore()
</script>