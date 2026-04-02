<template>
    <div class="h-full flex flex-col">
        <!-- Controls -->
        <div class="px-6 py-5 flex gap-3 flex-wrap">
            <!-- Search -->
            <div class="relative flex-1 min-w-50">
                <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-xs" :class="[style.text.muted]" />
                <input v-model="searchQuery" type="text" :placeholder="`${t('search')} ...`" class="w-full border text-sm
                 rounded-xl pl-9 pr-4 py-2.5 outline-none focus:border-indigo-500/60
                 transition-all duration-200"
                    :class="[style.bg.secondary, style.border.primary, style.text.primary]" />
            </div>

            <!-- Sort Dropdown -->
            <div class="relative">
                <select v-model="sortMode" class="appearance-none border text-sm
                 rounded-xl pl-4 pr-9 py-2.5 outline-none cursor-pointer
                 focus:border-indigo-500/60 transition-all duration-200"
                    :class="[style.text.primary, style.bg.secondary, style.border.primary,]">
                    <option value="newest">Hoạt động (mới → cũ)</option>
                    <option value="oldest">Hoạt động (cũ → mới)</option>
                    <option value="members_desc">Thành viên (nhiều → ít)</option>
                    <option value="members_asc">Thành viên (ít → nhiều)</option>
                </select>
                <span class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-white/40">
                    <i class="fas fa-sort-down"></i>
                </span>
            </div>

            <!-- Filter Dropdown -->
            <div class="relative">
                <select v-model="filterMode" class="appearance-none border text-sm 
                 rounded-xl pl-4 pr-9 py-2.5 outline-none cursor-pointer
                 focus:border-indigo-500/60 transition-all duration-200"
                    :class="[style.text.primary, style.bg.secondary, style.border.primary,]">
                    <option value="all">Tất cả</option>
                    <option value="large">Lớn (500+)</option>
                    <option value="medium">Vừa (200–499)</option>
                    <option value="small">Nhỏ (&lt;200)</option>
                </select>
                <span class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-white/40">
                    <i class="fas fa-arrows-alt"></i>
                </span>
            </div>
        </div>

        <!-- Group List -->
        <div class="px-6 space-y-1 pb-10 h-full overflow-auto">
            <TransitionGroup name="list" tag="div" class="space-y-1">
                <div v-for="group in filteredGroups" :key="group.id" class="group flex items-center gap-4 px-4 py-3 rounded-2xl
                 hover:bg-black/4 dark:hover:bg-white/5 active:bg-white/8 cursor-pointer
                 border border-slate-400 dark:border-slate-600
                 transition-all duration-200 ease-out">
                    <!-- Avatar -->
                    <div class="relative shrink-0">
                        <GroupAvatar :conversation="group" />
                    </div>

                    <!-- Info -->
                    <div class="flex-1 min-w-0">
                        <div class="flex items-center gap-1.5 min-w-0">
                            <!-- Group icon -->
                            <i class="fa fa-users" />
                            <p class="text-sm font-medium truncate" :class="[style.text.primary]">{{ group.name }}</p>
                        </div>
                        <p class="text-xs mt-0.5" :class="[style.text.primary]">{{ group.members?.length }} {{ t('member') }}
                        </p>
                    </div>

                    <!-- Menu button -->
                    <button class="shrink-0 p-1.5 rounded-lg text-white/20 hover:text-white/60
                   hover:bg-black/10 dark:hover:bg-white/10 transition-all cursor-pointer" @click.stop>
                        <i class="fas fa-ellipsis-h" :class="[style.text.primary]"></i>
                    </button>
                </div>
            </TransitionGroup>

            <!-- Empty state -->
            <div v-if="filteredGroups.length === 0" class="text-center py-16 text-white/30">
                <svg class="w-10 h-10 mx-auto mb-3 opacity-40" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                        d="M17 20h5v-2a3 3 0 0 0-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 0 1 5.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 0 1 9.288 0" />
                </svg>
                <p class="text-sm">{{ t('notFound') }}</p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { style } from '@/assets/tailwindcss'
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue'
import { useTranslate } from '@/composables/useTranslate'
import { useConversationStore } from '@/stores/conversation.storage'
import { ConversationType } from '@/types/entities'
import { ref, computed, onMounted } from 'vue'

const { t } = useTranslate()
const searchQuery = ref('')
const sortMode = ref<'newest' | 'oldest' | 'members_desc' | 'members_asc'>('newest')
const filterMode = ref<'all' | 'large' | 'medium' | 'small'>('all')
const convStorage = useConversationStore()

const groups = ref<ConversationType[]>([])

const filteredGroups = computed(() => {
    let list = [...groups.value]

    // Search
    if (searchQuery.value.trim()) {
        const q = searchQuery.value.toLowerCase()
        list = list.filter(g => g.name?.toLowerCase().includes(q))
    }

    // Filter
    if (filterMode.value === 'large') list = list.filter(g => g.members?.length >= 500)
    if (filterMode.value === 'medium') list = list.filter(g => g.members?.length >= 200 && g.members?.length < 500)
    if (filterMode.value === 'small') list = list.filter(g => g.members?.length < 200)

    // Sort
    if (sortMode.value === 'members_desc') list.sort((a, b) => b.members?.length - a.members?.length)
    if (sortMode.value === 'members_asc') list.sort((a, b) => a.members?.length - b.members?.length)
    if (sortMode.value === 'oldest') list.reverse()

    return list
})

const getGroups = async () => {
    groups.value = await convStorage.getGroups()
}

onMounted(() => {
    getGroups()
})
</script>

<style>
.list-enter-active,
.list-leave-active {
    transition: all 0.25s ease;
}

.list-enter-from {
    opacity: 0;
    transform: translateY(-8px);
}

.list-leave-to {
    opacity: 0;
    transform: translateX(12px);
}

.list-move {
    transition: transform 0.3s ease;
}
</style>