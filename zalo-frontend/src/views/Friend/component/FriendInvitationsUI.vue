<template>
  <div class="h-full flex flex-col bg-[#0f172a] text-white">
    <!-- CONTENT -->
    <div class="flex-1 overflow-y-auto p-4 space-y-6">
      <div class="text-sm text-gray-400 mb-3">
        Lời mời đã nhận ({{ received.length }})
      </div>
      <!-- EMPTY STATE -->
      <div
        v-if="received.length === 0"
        class="flex flex-col items-center justify-center py-10 text-center"
      >
        <img
          src="https://cdn-icons-png.flaticon.com/512/4076/4076478.png"
          class="w-32 opacity-80 mb-4"
        />
        <p class="text-gray-400 text-sm">Bạn không có lời mời nào</p>
      </div>

      <div v-else>
        <div
          v-for="friendship in received"
          :key="friendship.id"
          class="bg-[#1e293b] rounded-xl p-4 flex flex-col gap-3 hover:bg-[#263449] transition"
        >
          <!-- TOP -->
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <img
                :src="getOther(friendship).avatar?.url"
                class="w-10 h-10 rounded-full object-cover"
              />

              <div>
                <div class="font-medium text-sm">
                  {{ getOther(friendship).username }}
                </div>
                <div class="text-xs text-gray-400">
                  {{ formatSeparatorTime(friendship.ct) }}
                </div>
              </div>
            </div>

            <button class="p-2 rounded-lg hover:bg-gray-700 transition">
              <i class="fa-regular fa-message text-sm"></i>
            </button>
          </div>

          <!-- ACTION -->
          <button
            class="w-full py-2 rounded-lg cursor-pointer bg-gray-700 hover:bg-gray-600 text-sm font-medium transition"
            @click="acceptRequest(getOther(friendship).id)"
          >
            Xác nhận
          </button>
        </div>
      </div>

      <!-- SENT REQUESTS -->
      <div class="text-sm text-gray-400 mb-3">
        Lời mời đã gửi ({{ sent.length }})
      </div>
      <div v-if="sent.length > 0">
        <div
          v-for="friendship in sent"
          :key="friendship.id"
          class="bg-[#1e293b] rounded-xl p-4 flex flex-col gap-3 hover:bg-[#263449] transition"
        >
          <!-- TOP -->
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <img
                :src="getOther(friendship).avatar?.url"
                class="w-10 h-10 rounded-full object-cover"
              />

              <div>
                <div class="font-medium text-sm">
                  {{ getOther(friendship).username }}
                </div>
                <div class="text-xs text-gray-400">
                  {{ formatSeparatorTime(friendship.ct) }}
                </div>
              </div>
            </div>

            <button class="p-2 rounded-lg hover:bg-gray-700 transition">
              <i class="fa-regular fa-message text-sm"></i>
            </button>
          </div>

          <!-- ACTION -->
          <button
            class="w-full py-2 rounded-lg cursor-pointer bg-gray-700 hover:bg-gray-600 text-sm font-medium transition"
            @click="cancelRequest(getOther(friendship).id)"
          >
            Thu hồi lời mời
          </button>
        </div>
      </div>
      <div
        v-else
        class="flex flex-col items-center justify-center py-10 text-center"
      >
        <img
          src="https://cdn-icons-png.flaticon.com/512/4076/4076478.png"
          class="w-32 opacity-80 mb-4"
        />
        <p class="text-gray-400 text-sm">Bạn không có lời mời nào</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useDateTime } from "@/composables/useDateTime";
import { useFriendship } from "@/composables/useFriendship";
import { useFriendshipStore } from "@/stores/friendship.storage";
import { FriendshipType } from "@/types/entities";
import { onMounted, ref } from "vue";

const friendshipStorage = useFriendshipStore();
const { getOther } = useFriendship();
const { formatSeparatorTime } = useDateTime();

const isLoadingRecieved = ref(false);
const isLoadingSent = ref(false);

const received = ref<FriendshipType[]>([]); // lời mời nhận (đang empty)

const sent = ref<FriendshipType[]>([]);

const cancelRequest = (id: number) => {
  // sent.value = sent.value.filter((u) => u.id !== id);
};

const acceptRequest = (id: number) => {};

onMounted(async () => {
  isLoadingRecieved.value = true;
  received.value = await friendshipStorage.getRecieved();
  isLoadingRecieved.value = false;

  isLoadingSent.value = true;
  sent.value = await friendshipStorage.getSent();
  isLoadingSent.value = false;
});
</script>
