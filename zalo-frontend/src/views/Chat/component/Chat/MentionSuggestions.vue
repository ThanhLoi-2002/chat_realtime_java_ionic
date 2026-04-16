<template>
    <div v-if="suggestions.length > 0"
        class="absolute bg-white dark:bg-gray-800 border border-gray-200 dark:border-slate-700 rounded-xl shadow-2xl z-9999 max-h-48 overflow-y-auto w-fit min-w-50 bottom-full left-8">
        <div v-for="(user, index) in suggestions" :key="user.id" @mousedown.prevent="$emit('select', user)"
            class="flex items-center gap-2 px-4 py-3 cursor-pointer border-b border-gray-100 dark:border-slate-700 last:border-0"
            :class="{ 'bg-blue-100 dark:bg-slate-700': index === selectedIndex }">
            <CircleAvatar :user="user" :is-disabled="true" size="w-8 h-8" />
            <span class="text-xs dark:text-slate-200" :class="{ 'font-bold text-blue-600': index === selectedIndex }">
                @{{ user.username }}
            </span>
        </div>
    </div>
</template>

<script setup lang="ts">
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
defineProps<{ suggestions: any[], selectedIndex: number }>();
defineEmits(['select']);
</script>