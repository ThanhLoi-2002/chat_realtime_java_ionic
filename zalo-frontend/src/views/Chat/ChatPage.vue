<script setup lang="ts">
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import BaseButton from '@/components/Button/BaseButton.vue';
import { useTranslate } from '@/composables/useTranslate';
import { RANDOM_AVATAR } from '@/utils/constant';
import { connectSocket } from '@/utils/websocket';
import { onMounted, ref } from 'vue';
import ConversationUI from './component/ConversationUI.vue';
import { userApi } from '@/api/user.api';
import { UserType } from '@/types/entities';

type Conversation = {
  id: number
  // thêm field nếu có
}


const { t } = useTranslate()
const selectedConversation = ref<Conversation | null>(null)

const conversations = ref([
  {
    id: 1,
  },
  {
    id: 2,
  }
])

const selectConversation = (item: any) => {
  selectedConversation.value = item
}

onMounted(async () => {
  connectSocket()
})

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

<template>

  <div class="flex h-screen bg-white dark:bg-gray-800">
    <!-- CHAT LIST -->
    <section :class="[
      'flex-1 sm:flex-none w-88 max-w-160 border-r border-slate-200 dark:border-slate-500 flex flex-col',
      selectedConversation ? 'hidden sm:flex' : 'flex'
    ]">

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
        <conversation-u-i v-for="item in conversations" :key="item.id" @click="selectConversation(item)"
          :conversation="item" :selectedConversation="selectedConversation" />
      </div>

    </section>

    <div v-if="!selectedConversation" class="hidden sm:flex flex-1 items-center justify-center text-gray-400">
      Select a conversation
    </div>

    <!-- CHAT AREA -->
    <main v-else :class="[
      'flex-1 flex flex-col bg-gray-100 dark:bg-gray-900',
      selectedConversation ? 'flex' : 'hidden sm:flex'
    ]">

      <!-- HEADER -->
      <header class="h-16 flex items-center justify-between px-6
          bg-white dark:bg-gray-900
          border-b border-gray-200 dark:border-slate-500">

        <div class="flex items-center gap-3">
          <!-- Back button (mobile only) -->
          <button class="sm:hidden text-gray-600 dark:text-white" @click="selectedConversation = null">
            <i class="fas fa-arrow-circle-left"></i>
          </button>
          <circle-avatar :url="RANDOM_AVATAR" size="size-10" :onClick="() => { }" />

          <div class="flex flex-col">
            <span class="font-medium dark:text-slate-200">
              Alice Johnson
            </span>
            <span class="text-xs bg-green-400 rounded-full w-2.5 h-2.5">
            </span>
          </div>
        </div>
      </header>

      <!-- MESSAGES -->
      <div class="flex-1 overflow-y-auto p-6 space-y-4">
        <!-- received -->
        <div class="flex gap-2 items-center">
          <circle-avatar :url="RANDOM_AVATAR" size="size-8" :onClick="() => { }" />
          <div
            class="bg-white dark:bg-gray-800 dark:text-slate-100 px-4 py-2 rounded-xl text-sm border border-slate-300">
            Hi! Did you read the brief?
          </div>
        </div>

        <!-- sent -->
        <div class="flex items-end gap-2 flex-row-reverse">
          <div
            class="bg-white dark:bg-gray-800 dark:text-slate-100 px-4 py-2 rounded-xl text-sm border border-slate-300">
            Yes, looks good!
          </div>
        </div>
      </div>

      <!-- INPUT -->
      <footer class="p-4 border-t
          border-gray-200 dark:border-slate-500
          bg-white dark:bg-gray-900">

        <div class="flex gap-3">
          <input :placeholder="t('typeMessage')" class="flex-1 px-4 py-1.5 rounded-lg
                bg-gray-100 dark:bg-gray-800 dark:text-slate-200" />

          <base-button icon="fa-solid fa-paper-plane text-blue-500" />
        </div>

      </footer>

    </main>

  </div>
</template>