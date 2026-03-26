<template>
    <div class="flex h-screen bg-gray-100 dark:bg-[#1f2937] text-gray-900 dark:text-white">

        <!-- SIDEBAR (DESKTOP) -->
        <div class="hidden md:flex w-64 bg-white dark:bg-[#111827]
                border-r border-gray-200 dark:border-gray-700 flex-col">
            <FriendSidebar />
        </div>

        <!-- MOBILE DRAWER -->
        <div v-if="showSidebar" class="fixed inset-0 z-50 flex md:hidden">

            <!-- overlay -->
            <div class="flex-1 bg-black/50" @click="showSidebar = false" />

            <!-- sidebar -->
            <div class="w-64 bg-white dark:bg-[#111827] shadow-xl">
                <FriendSidebar />
            </div>
        </div>

        <!-- MAIN -->
        <div class="flex-1 flex flex-col h-[90vh]">

            <!-- HEADER -->
            <div class="h-14 flex items-center px-4 md:px-6 gap-3
                  border-b border-gray-200 dark:border-gray-700
                  bg-white dark:bg-[#1f2937]">

                <!-- menu button -->
                <button class="md:hidden p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition"
                    @click="showSidebar = true">
                    <i class="fa fa-bars text-lg"></i>
                </button>

                <span class="font-semibold text-base md:text-lg">
                    Danh sách bạn bè
                </span>
            </div>

            <!-- CONTENT -->
            <div class="flex-1 flex flex-col h-full">

                <!-- TOP BAR (KHÔNG SCROLL) -->
                <div class="p-3 md:p-4 pb-2">

                    <div class="flex flex-col md:flex-row gap-2 md:gap-3">

                        <div class="flex-1 bg-gray-100 dark:bg-gray-800
                  rounded-lg px-3 py-2 flex items-center gap-2">
                            <i class="fa fa-search text-gray-500"></i>
                            <input v-model="keyword" placeholder="Tìm bạn" class="bg-transparent outline-none text-sm w-full
                 text-gray-900 dark:text-white placeholder-gray-400" />
                        </div>

                    </div>
                </div>

                <!-- LIST (SCROLL) -->
                <div class="flex-1 overflow-y-auto px-3 md:px-4 pb-4">

                    <div v-for="(group, letter) in groupedFriends" :key="letter">

                        <!-- LETTER -->
                        <div class="text-gray-500 dark:text-gray-400 text-xs md:text-sm mb-2 mt-4">
                            {{ letter }}
                        </div>

                        <!-- USERS -->
                        <div v-for="u in group" :key="u.id" class="flex items-center justify-between py-2 rounded-lg cursor-pointer
               transition-all duration-200
               hover:bg-gray-100 dark:hover:bg-gray-800 hover:scale-[1.01]">
                            <div class="flex items-center gap-3">
                                <img :src="u.avatar || defaultAvatar"
                                    class="w-9 h-9 md:w-10 md:h-10 rounded-full object-cover" />

                                <span class="text-sm md:text-base font-medium">
                                    {{ u.username }}
                                </span>
                            </div>

                            <button class="p-2 rounded-lg
                 hover:bg-gray-200 dark:hover:bg-gray-700
                 transition">
                                <i class="fa-solid fa-ellipsis text-sm"></i>
                            </button>
                        </div>

                    </div>

                </div>

            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue"
import FriendSidebar from "./component/FriendSidebar.vue"

const keyword = ref("")
const showSidebar = ref(false)

// mock data
const friends = ref([
    { id: 1, username: "Bảo Ngọc" },
    { id: 2, username: "Buubuu Au" },
    { id: 3, username: "Doan Nguyet" },
    { id: 4, username: "Doantbphuong" },
    { id: 5, username: "Doanthanhdanh" },
    { id: 6, username: "Hang Nguyen" },
])

// filter
const filtered = computed(() =>
    friends.value.filter(f =>
        f.username.toLowerCase().includes(keyword.value.toLowerCase())
    )
)

// group theo chữ cái
const groupedFriends = computed(() => {
    const groups: any = {}

    filtered.value.forEach(u => {
        const letter = u.username.charAt(0).toUpperCase()
        if (!groups[letter]) groups[letter] = []
        groups[letter].push(u)
    })

    return groups
})

const defaultAvatar =
    "https://cdn-icons-png.flaticon.com/512/149/149071.png"
</script>