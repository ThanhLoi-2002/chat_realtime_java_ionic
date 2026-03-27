<template>
    <div class="h-full flex flex-col bg-[#0f172a] text-white">
        <!-- CONTENT -->
        <div class="flex-1 overflow-y-auto p-4 space-y-6">

            <!-- EMPTY STATE -->
            <div v-if="received.length === 0" class="flex flex-col items-center justify-center py-10 text-center">
                <img src="https://cdn-icons-png.flaticon.com/512/4076/4076478.png" class="w-32 opacity-80 mb-4" />
                <p class="text-gray-400 text-sm">
                    Bạn không có lời mời nào
                </p>
            </div>

            <!-- SENT REQUESTS -->
            <div v-if="sent.length > 0">
                <div class="text-sm text-gray-400 mb-3">
                    Lời mời đã gửi ({{ sent.length }})
                </div>

                <div v-for="user in sent" :key="user.id" class="bg-[#1e293b] rounded-xl p-4 flex flex-col gap-3
                 hover:bg-[#263449] transition">
                    <!-- TOP -->
                    <div class="flex items-center justify-between">
                        <div class="flex items-center gap-3">
                            <img :src="user.avatar" class="w-10 h-10 rounded-full object-cover" />

                            <div>
                                <div class="font-medium text-sm">
                                    {{ user.name }}
                                </div>
                                <div class="text-xs text-gray-400">
                                    {{ user.time }}
                                </div>
                            </div>
                        </div>

                        <button class="p-2 rounded-lg hover:bg-gray-700 transition">
                            <i class="fa-regular fa-message text-sm"></i>
                        </button>
                    </div>

                    <!-- ACTION -->
                    <button class="w-full py-2 rounded-lg cursor-pointer
                   bg-gray-700 hover:bg-gray-600
                   text-sm font-medium transition" @click="cancelRequest(user.id)">
                        Thu hồi lời mời
                    </button>
                </div>
            </div>

        </div>
    </div>
</template>

<script setup lang="ts">
import { useFriendshipStore } from "@/stores/friendship.storage"
import { onMounted, ref } from "vue"

const friendshipStorage = useFriendshipStore()

const received = ref([]) // lời mời nhận (đang empty)

const sent = ref([
    {
        id: 1,
        name: "Nguyễn Tloc",
        avatar: "https://i.pravatar.cc/150?img=3",
        time: "Vài giây",
    },
])

const cancelRequest = (id: number) => {
    sent.value = sent.value.filter(u => u.id !== id)
}

onMounted(() => {
    friendshipStorage.getRecieved()
    friendshipStorage.getSent()
})
</script>