<template>
    <div class="p-4 dark:text-white flex flex-col gap-3">
        <div class="flex justify-between gap-2">
          <span class="text-xl font-light">
            {{ t('message') }}
          </span>
          <ion-button @click="showModal = true">
            Thêm bạn
          </ion-button>
          <ion-button @click="showModal = true">
            Thêm bạn
          </ion-button>
        </div>

        <ion-modal :is-open="showModal">
          <div class="p-4 bg-white dark:bg-gray-800 h-full flex flex-col">

            <!-- Header -->
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-lg font-semibold">Thêm bạn</h2>
              <button @click="showModal = false">✕</button>
            </div>

            <!-- Input -->
            <input v-model="phone" placeholder="Số điện thoại" class="border-b p-2 outline-none bg-transparent" />

            <!-- Result -->
            <div v-if="user" class="mt-4 flex items-center gap-3 cursor-pointer" @click="goToProfile(user?.id)">
              <img :src="user?.avatar?.url" class="w-12 h-12 rounded-full" />
              <div>
                <p>{{ user.username }}</p>
                <p class="text-sm text-gray-500">{{ user.phone }}</p>
              </div>
            </div>

            <!-- Footer -->
            <div class="mt-auto flex justify-end gap-2">
              <button @click="showModal = false">Huỷ</button>
              <button @click="searchUser" class="bg-blue-500 text-white px-4 py-2 rounded">
                Tìm kiếm
              </button>
            </div>

          </div>
        </ion-modal>


        <input :placeholder="t('search') + '...'" class="w-full px-4 py-2 rounded-lg
         bg-white text-gray-800
         dark:bg-gray-800 dark:text-gray-200
         placeholder-gray-400 dark:placeholder-gray-500
         border border-gray-200 dark:border-gray-700
         focus:outline-none focus:ring-1 focus:ring-blue-400" />
      </div>

      <div class="flex-1 overflow-y-auto">
        <conversation-u-i v-for="item in conversationStorage.conversations" :key="item.id" @click="conversationStorage.selectConversation(item)"
          :conversation="item" :selectedConversation="conversationStorage.conversation" />
      </div>
</template>
<script setup lang="ts">
import { userApi } from '@/api/user.api';
import { useTranslate } from '@/composables/useTranslate';
import { ConversationType, UserType } from '@/types/entities';
import { ref } from 'vue';
import ConversationUI from './component/ConversationUI.vue';
import { useConversationStore } from '@/stores/conversation.storage';

const { t } = useTranslate()

const conversationStorage = useConversationStore()

const showModal = ref(false)
const phone = ref("")
const user = ref<UserType | null>(null)

const searchUser = async () => {
  try {
    const data: any = await userApi.getList(phone.value);
    console.log(data)
    user.value = data.result.content[0] ?? null
  } catch (e) {
    user.value = null
    alert("Không tìm thấy user")
  }
}

const goToProfile = (id?: number) => {
  showModal.value = false
  // router.push(`/user/${id}`)
}
</script>