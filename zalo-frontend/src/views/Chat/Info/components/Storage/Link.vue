<template>
    <div>
        <!-- SEARCH -->
        <div class="p-3">
            <div class="flex items-center bg-[#374151] rounded-full px-3 py-2">
                <i class="fa fa-search text-gray-400 mr-2"></i>
                <input placeholder="Tìm kiếm link"
                    class="bg-transparent outline-none text-sm w-full placeholder-gray-400" />
            </div>
        </div>

        <!-- FILTER -->
        <div class="flex gap-2 px-3 pb-2">
            <select v-model="filterUser" class="bg-[#374151] text-xs px-3 py-1 rounded-full outline-none">
                <option value="">Người gửi</option>
                <option value="me">Tôi</option>
                <option value="other">Người khác</option>
            </select>

            <select v-model="filterDate" class="bg-[#374151] text-xs px-3 py-1 rounded-full outline-none">
                <option value="">Ngày gửi</option>
                <option value="today">Hôm nay</option>
                <option value="week">7 ngày</option>
                <option value="month">30 ngày</option>
            </select>
        </div>

        <!-- LIST -->
        <div class="flex-1 overflow-y-auto px-3 pb-4 space-y-5">

            <div v-for="group in groupedLinks" :key="group.date">

                <!-- DATE -->
                <div class="text-gray-400 text-sm font-semibold mb-2">
                    {{ group.date }}
                </div>

                <!-- ITEMS -->
                <div class="space-y-3">
                    <div v-for="item in group.items" :key="item.id" class="flex items-center gap-3 cursor-pointer">

                        <!-- THUMB / ICON -->
                        <div class="w-12 h-12 rounded-lg overflow-hidden bg-[#374151] flex items-center justify-center">

                            <!-- nếu có thumbnail -->
                            <img v-if="item.thumbnail" :src="item.thumbnail" class="w-full h-full object-cover" />

                            <!-- fallback icon -->
                            <i v-else class="fa fa-link text-gray-300"></i>
                        </div>

                        <!-- INFO -->
                        <div class="flex-1 min-w-0">
                            <div class="text-sm font-medium truncate">
                                {{ item.title }}
                            </div>

                            <div class="text-xs text-blue-400 truncate mt-1">
                                {{ item.domain }}
                            </div>
                        </div>

                    </div>
                </div>

            </div>

        </div>
    </div>
</template>
<script setup lang="ts">
import { computed, ref } from 'vue'
import { useDateTime } from '@/composables/useDateTime'

const { formatDate } = useDateTime()

const filterUser = ref('')
const filterDate = ref('')

const links = [
    {
        id: 1,
        title: 'tại Đà Nẵng – My Blog',
        domain: 'hongngay.com',
        ct: new Date(),
        thumbnail: null,
        isMe: true
    },
    {
        id: 2,
        title: 'Hướng dẫn tân thủ',
        domain: 'docs.google.com',
        ct: new Date(),
        thumbnail: 'https://via.placeholder.com/100',
        isMe: false
    }
]

const filteredLinks = computed(() => {
    return links.filter(item => {

        if (filterUser.value === 'me' && !item.isMe) return false
        if (filterUser.value === 'other' && item.isMe) return false

        const now = new Date()
        const d = new Date(item.ct)

        if (filterDate.value === 'today' && d.toDateString() !== now.toDateString()) {
            return false
        }

        if (filterDate.value === 'week') {
            const diff = (now.getTime() - d.getTime()) / 86400000
            if (diff > 7) return false
        }

        if (filterDate.value === 'month') {
            const diff = (now.getTime() - d.getTime()) / 86400000
            if (diff > 30) return false
        }

        return true
    })
})

const groupedLinks = computed(() => {
    const map: Record<string, any[]> = {}

    filteredLinks.value.forEach(item => {
        const date = formatDate(item.ct)
        if (!map[date]) map[date] = []
        map[date].push(item)
    })

    return Object.keys(map).map(date => ({
        date,
        items: map[date]
    }))
})
</script>
