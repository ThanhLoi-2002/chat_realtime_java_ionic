<template>
  <div class="px-4 py-3 dark:text-white flex flex-col gap-3">
    <div class="flex justify-between items-center gap-2">
      <span class="text-lg font-light">
        {{ t('message') }}
      </span>
      <div class="flex gap-2">
        <ion-button @click="goScan" size="medium">
          <i class="fas fa-qrcode text-[10px]"></i>
        </ion-button>
        <ion-button :onClick="() => openModal('addFriend')" size="medium">
          <i class="fas fa-user-plus text-[10px]"></i>
        </ion-button>
        <ion-button :onClick="() => openModal('createGroup')" size="medium">
          <i class="fa-solid fa-users-line text-[10px]"></i>
        </ion-button>
      </div>
    </div>

    <modal ref="modalRef" :title="t((pageModal == 'friendProfile') ? 'profile' : pageModal)"
      :go-back="() => goPage('addFriend')" :isDisplayBackButton="pageModal != Object.keys(pages)[0]">
      <transition name="slide" mode="out-in">
        <!-- <KeepAlive> -->
        <component :is="pages[pageModal]" :key="pageModal" :goPage="goPage" :setUser="(u: UserType) => selectedUser = u"
          :user="selectedUser" />
        <!-- </KeepAlive> -->
      </transition>
    </modal>

    <input v-model="keyword" :placeholder="t('search') + '...'" class="w-full px-4 py-2 rounded-lg
         bg-white text-gray-800 text-base
         dark:bg-gray-800 dark:text-gray-200
         placeholder-gray-400 dark:placeholder-gray-500
         border border-gray-200 dark:border-gray-700
         focus:outline-none focus:ring-1 focus:ring-blue-400" />
  </div>

  <header-filter v-if="!isSmartDevice()" v-model="selectedClassCards" />

  <div ref="conversationRef" class="flex-1 overflow-y-auto max-h-[73%] sm:max-h-full" @scroll="scrollMore">
    <conversation-u-i v-for="item in displayConversations" :key="item.id" @click="selectConversation(item)"
      :conversation="item" :selectedConversation="convStorage.conversation" />
  </div>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { computed, defineAsyncComponent, onMounted, ref, watch } from 'vue';
import ConversationUI from './component/ConversationUI.vue';
import { useConversationStore } from '@/stores/conversation.storage';
import { SearchFriendPageType } from '@/types/common';
import Modal from '@/components/Modal/Modal.vue';
import { ClassificationCardType, ConversationType, UserType } from '@/types/entities';
import { useScroll } from '@/composables/useScroll';
import { useRouter } from 'vue-router';
import { useDevice } from '@/composables/useDevice';
import HeaderFilter from './component/HeaderFilter.vue';

const CreateGroupUI = defineAsyncComponent(() => import('./component/CreateGroupUI.vue'));
const AddFriendUI = defineAsyncComponent(() => import('./component/AddFriendUI.vue'));
const FriendProfileUI = defineAsyncComponent(() => import('./component/FriendProfileUI.vue'));

const { t } = useTranslate()
const { onScroll } = useScroll()
const { isSmartDevice } = useDevice()

const convStorage = useConversationStore()
const selectedUser = ref<UserType | null>(null)
const modalRef = ref()
const router = useRouter()
const keyword = ref("");
const selectedClassCards = ref<ClassificationCardType[]>([])

const pageModal = ref<SearchFriendPageType | "createGroup">("addFriend")
const pages = {
  addFriend: AddFriendUI,
  friendProfile: FriendProfileUI,
  createGroup: CreateGroupUI
}

const conversationRef = ref<HTMLElement | null>(null)

const scrollMore = () => {
  if (convStorage.isLoading || !convStorage.hasMore) return
  onScroll(conversationRef.value, () =>
    convStorage.getConversations()
  )
}

const goScan = () => {
  router.push('/scan')
}

const goPage = (page: SearchFriendPageType | "createGroup") => {
  pageModal.value = page
}

const selectConversation = (item: ConversationType) => {
  convStorage.selectConversation(item)
}

const openModal = (page: SearchFriendPageType | "createGroup") => {
  goPage(page)
  modalRef?.value.present()
}

// Danh sách hiển thị ưu tiên hàng đã lọc
const displayConversations = computed(() => {
  return (keyword.value || selectedClassCards.value.length > 0)
    ? convStorage.filteredConvs
    : convStorage.conversations;
});

// Hàm xử lý lọc tổng hợp
const handleFilter = () => {
  // Trải phẳng tất cả conversationIds từ các card đang chọn
  const allIds = selectedClassCards.value.flatMap(card => card.conversationIds || []);

  // Tạo Set từ mảng đã trải phẳng (Set sẽ tự động loại bỏ các ID trùng lặp)
  const allowedIds = new Set(allIds);

  // Gọi hàm lọc trong store
  convStorage.setFilter({
    name: keyword.value,
    convIds: Array.from(allowedIds)
  });
};

// Quan trọng: Khi danh sách tag thay đổi, cũng phải chạy lại hàm lọc
watch([keyword, selectedClassCards], () => {
  handleFilter();
}, { deep: true });

onMounted(() => {
  convStorage.getConversations()
})
</script>