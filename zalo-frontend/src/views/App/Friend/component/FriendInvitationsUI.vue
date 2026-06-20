<template>
  <div class="h-full flex flex-col dark:bg-[#0f172a] text-white">
    <!-- CONTENT -->
    <div class="flex-1 overflow-y-auto p-4 space-y-6">
      <div class="text-sm text-gray-500 mb-3">
        Lời mời đã nhận ({{ received.length }})
      </div>
      <!-- EMPTY STATE -->
      <div v-if="received.length === 0" class="flex flex-col items-center justify-center py-10 text-center">
        <img src="https://cdn-icons-png.flaticon.com/512/4076/4076478.png" class="w-32 opacity-80 mb-4" />
        <p class="text-gray-500 text-sm">Bạn không có lời mời nào</p>
      </div>

      <div v-else>
        <div v-for="friendship in received" :key="friendship.id"
          class="bg-[#1e293b] rounded-xl p-4 flex flex-col gap-3 hover:bg-[#263449] transition">
          <!-- TOP -->
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <CircleAvatar :user="getOther(friendship)"
                custom-class="w-10 h-10 rounded-full object-cover cursor-pointer" />

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
          <div class="flex gap-4">
            <!-- Nút xác nhận -->
            <button class="w-full py-2 rounded-lg cursor-pointer 
           bg-blue-600 hover:bg-blue-500 
           text-white text-sm font-medium transition" @click="openConfirmModal('accept', friendship)">
              Xác nhận
            </button>

            <!-- Nút hủy -->
            <button class="w-full py-2 rounded-lg cursor-pointer 
           bg-red-600 hover:bg-red-500 
           text-white text-sm font-medium transition" @click="openConfirmModal('reject', friendship)">
              {{ t("cancel") }}
            </button>
          </div>

        </div>
      </div>

      <!-- SENT REQUESTS -->
      <div class="text-sm text-gray-500 mb-3">
        Lời mời đã gửi ({{ sent.length }})
      </div>
      <div v-if="sent.length > 0">
        <div v-for="friendship in sent" :key="friendship.id"
          class="bg-[#1e293b] rounded-xl p-4 flex flex-col gap-3 hover:bg-[#263449] transition">
          <!-- TOP -->
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <CircleAvatar :user="getOther(friendship)"
                custom-class="w-10 h-10 rounded-full object-cover cursor-pointer" />

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
            @click="openConfirmModal('cancel', friendship)">
            {{ t("revokeInvitation") }}
          </button>
        </div>
      </div>
      <div v-else class="flex flex-col items-center justify-center py-10 text-center">
        <img src="https://cdn-icons-png.flaticon.com/512/4076/4076478.png" class="w-32 opacity-80 mb-4" />
        <p class="text-gray-500 text-sm">Bạn không có lời mời nào</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useDateTime } from "@/composables/useDateTime";
import { useFriendship } from "@/composables/useFriendship";
import { useTranslate } from "@/composables/useTranslate";
import { FriendshipType } from "@/types/entities";
import { onMounted, ref } from "vue";
import CircleAvatar from "@/components/Shared/Avatar/CircleAvatar.vue";
import { useConfirmStore } from "@/composables/useConfirm";
import { useFriendshipStore } from "@/stores/App/friendship.storage";

const friendshipStorage = useFriendshipStore();
const { getOther } = useFriendship();
const { formatSeparatorTime } = useDateTime();
const { t } = useTranslate()

const isLoadingRecieved = ref(false);
const isLoadingSent = ref(false);
const selectedFriendship = ref<FriendshipType | undefined>(undefined)
const confirmStore = useConfirmStore();

const received = ref<FriendshipType[]>([]); // lời mời nhận (đang empty)

const sent = ref<FriendshipType[]>([]);

const cancelRequest = async (otherId: number) => {
  const isSuccess = await friendshipStorage.cancel(otherId)

  if (isSuccess)
    sent.value = sent.value.filter((f) => f.id !== selectedFriendship.value!.id);
};

const acceptRequest = async (otherId: number) => {
  const isSuccess = await friendshipStorage.accept(otherId)

  if (isSuccess)
    received.value = sent.value.filter((f) => f.id !== selectedFriendship.value!.id);
};

const rejectRequest = async (otherId: number) => {
  const isSuccess = await friendshipStorage.reject(otherId)

  if (isSuccess)
    received.value = sent.value.filter((f) => f.id !== selectedFriendship.value!.id);
};

const openConfirmModal = (type: 'accept' | 'cancel' | 'reject', item: FriendshipType) => {
  const otherId = getOther(selectedFriendship.value!).id
  let onConfirm
  let title
  let message

  switch (type) {
    case 'accept':
      title = 'acceptFriend'
      message = 'confirmAcceptRequest'
      onConfirm = () => acceptRequest(otherId)
      break;
    case 'cancel':
      title = 'cancleAddFriend'
      message = 'confirmCancelRequest'
      onConfirm = () => cancelRequest(otherId)
      break
    case 'reject':
      title = 'rejectAddFriend'
      message = 'confirmRejectRequest'
      onConfirm = () => cancelRequest(otherId)
      break
  }

  confirmStore.open({
    title: t(title),
    message: t(message),
    onOk: onConfirm
  });
}

onMounted(async () => {
  isLoadingRecieved.value = true;
  received.value = await friendshipStorage.getRecieved();
  isLoadingRecieved.value = false;

  isLoadingSent.value = true;
  sent.value = await friendshipStorage.getSent();
  isLoadingSent.value = false;
});
</script>
