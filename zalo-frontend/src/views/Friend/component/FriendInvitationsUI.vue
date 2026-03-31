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
              <CircleAvatar :user="getOther(friendship)" custom-class="w-10 h-10 rounded-full object-cover cursor-pointer"/>

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
              <CircleAvatar :user="getOther(friendship)" custom-class="w-10 h-10 rounded-full object-cover cursor-pointer"/>

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

  <!-- CONFIRM: CANCEL -->
  <ConfirmModal v-model:showConfirm="showConfirmCancel" :onOk="cancelRequest" :message="t('confirmCancelRequest')"
    :header="t('cancleAddFriend')" />

  <ConfirmModal v-model:showConfirm="showConfirmAccept" :onOk="acceptRequest" :message="t('confirmAcceptRequest')"
    :header="t('acceptFriend')" />

  <!-- CONFIRM: REJECT -->
  <ConfirmModal v-model:showConfirm="showConfirmReject" :onOk="rejectRequest" :message="t('confirmRejectRequest')"
    :header="t('rejectAddFriend')" />
</template>

<script setup lang="ts">
import ConfirmModal from "@/components/Modal/ConfirmModal.vue";
import { useDateTime } from "@/composables/useDateTime";
import { useFriendship } from "@/composables/useFriendship";
import { useTranslate } from "@/composables/useTranslate";
import { useFriendshipStore } from "@/stores/friendship.storage";
import { FriendshipType } from "@/types/entities";
import { onMounted, ref } from "vue";
import CircleAvatar from "@/components/Avatar/CircleAvatar.vue";

const friendshipStorage = useFriendshipStore();
const { getOther } = useFriendship();
const { formatSeparatorTime } = useDateTime();
const { t } = useTranslate()

const isLoadingRecieved = ref(false);
const isLoadingSent = ref(false);
const selectedFriendship = ref<FriendshipType | undefined>(undefined)

const showConfirmCancel = ref(false)
const showConfirmAccept = ref(false)
const showConfirmReject = ref(false)

const received = ref<FriendshipType[]>([]); // lời mời nhận (đang empty)

const sent = ref<FriendshipType[]>([]);

const cancelRequest = async () => {
  const otherId = getOther(selectedFriendship.value!).id
  const isSuccess = await friendshipStorage.cancel(otherId)

  if (isSuccess)
    sent.value = sent.value.filter((f) => f.id !== selectedFriendship.value!.id);

  console.log(showConfirmAccept.value)
};

const acceptRequest = async () => {
  const otherId = getOther(selectedFriendship.value!).id
  const isSuccess = await friendshipStorage.accept(otherId)

  if (isSuccess)
    received.value = sent.value.filter((f) => f.id !== selectedFriendship.value!.id);
};

const rejectRequest = async () => {
  const otherId = getOther(selectedFriendship.value!).id
  const isSuccess = await friendshipStorage.reject(otherId)

  if (isSuccess)
    received.value = sent.value.filter((f) => f.id !== selectedFriendship.value!.id);
};

const openConfirmModal = (type: 'accept' | 'cancel' | 'reject', item: FriendshipType) => {
  selectedFriendship.value = item

  switch (type) {
    case 'accept':
      showConfirmAccept.value = true
      break;
    case 'cancel':
      showConfirmCancel.value = true
      break
    case 'reject':
      showConfirmReject.value = true
      break
  }
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
