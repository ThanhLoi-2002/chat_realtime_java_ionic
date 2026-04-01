<template lang="">
    <div>
    <!-- FILTER -->
        <div class="flex gap-2 p-2 border-b border-slate-700">

            <!-- FILTER USER -->
            <select v-model="filterUser" class="px-3 py-1 bg-slate-200 text-slate-500 dark:bg-slate-600 dark:text-slate-300 rounded-full text-xs outline-none">
                <option value="">Người gửi</option>
                <option value="me">Tôi</option>
                <option value="other">Người khác</option>
            </select>

            <!-- FILTER DATE -->
            <select v-model="filterDate" class="px-3 py-1 bg-slate-200 text-slate-500 dark:bg-slate-600 dark:text-slate-300 rounded-full text-xs outline-none">
                <option value="">Ngày gửi</option>
                <option value="today">Hôm nay</option>
                <option value="week">7 ngày qua</option>
                <option value="month">30 ngày qua</option>
            </select>

        </div>

        <!-- MEDIA LIST -->
        <div class="p-2 space-y-4">

            <div v-for="group in groupedMedia" :key="group.date">

                <!-- DATE -->
                <div class="text-gray-400 font-bold text-sm mb-2">
                    {{ group.date }}
                </div>

                <!-- GRID -->
                <div class="grid grid-cols-3 gap-2">
                    <div v-for="item in group.items" :key="item.id"
                        class="aspect-square rounded overflow-hidden relative group cursor-pointer">
                        <img :src="item.file.url" class="w-full h-full object-cover transition group-hover:scale-105" />

                        <!-- hover overlay -->
                        <div class="absolute inset-0 bg-black/30 opacity-0 group-hover:opacity-100 transition"></div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { useDateTime } from '@/composables/useDateTime'
import { useConversationStore } from '@/stores/conversation.storage'
import { MessageType } from '@/types/entities'
import { computed, ref } from 'vue'

const convStorage = useConversationStore()
const { formatDate } = useDateTime()

const groupedMedia = computed(() => {
    const map: Record<string, MessageType[]> = {}

    convStorage.images.forEach(item => {
        const ct = formatDate(item.ct)
        if (!map[ct]) map[ct] = []
        map[ct].push(item)
    })

    return Object.keys(map).map(date => ({
        date: date,
        items: map[date]
    }))
})

const filterUser = ref('')
const filterDate = ref('')

const filteredImages = computed(() => {
    return convStorage.images.filter(item => {

        // filter user
        // if (filterUser.value === 'me' && !item.isMe) return false
        // if (filterUser.value === 'other' && item.isMe) return false

        // filter date
        const now = new Date()
        const itemDate = new Date(item.ct)

        if (filterDate.value === 'today') {
            if (itemDate.toDateString() !== now.toDateString()) return false
        }

        if (filterDate.value === 'week') {
            const diff = (now.getTime() - itemDate.getTime()) / (1000 * 60 * 60 * 24)
            if (diff > 7) return false
        }

        if (filterDate.value === 'month') {
            const diff = (now.getTime() - itemDate.getTime()) / (1000 * 60 * 60 * 24)
            if (diff > 30) return false
        }

        return true
    })
})

</script>