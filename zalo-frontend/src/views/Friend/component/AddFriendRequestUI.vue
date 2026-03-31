<template lang="">
  <div class="h-[85%]">
    <div class="relative">
      <div class="h-36 cursor-pointer">
        <img
          :src="user?.cover?.url ?? RANDOM_AVATAR"
          class="w-full h-full rounded-lg"
        />
      </div>

      <!-- Avatar center -->
      <div class="absolute left-1/2 -bottom-12 -translate-x-1/2">
        <div class="relative w-24 h-24">
          <!-- Avatar -->
          <CircleAvatar
            :user="user"
            custom-class="w-24 h-24 rounded-full ring-2 ring-white dark:ring-slate-800 overflow-hidden bg-gray-300"
            :is-disabled=true
          />
        </div>
      </div>
    </div>

    <!-- BODY -->
    <div class="pt-10 pb-4 px-2 dark:bg-gray-800 overflow-auto h-[75%]">
      <!-- Name -->
      <div class="flex items-center justify-center gap-2">
        <h2 class="text-lg font-semibold dark:text-slate-300">
          {{ user?.username }}
        </h2>
        <i
          class="fa fa-pencil text-sm cursor-pointer text-gray-500 dark:text-gray-400"
        ></i>
      </div>

      <!-- INFO -->
      <div class="w-full">
        <!-- TEXTAREA WRAPPER -->
        <div
          class="relative rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 focus-within:ring-2 focus-within:ring-blue-500 transition"
        >
          <!-- TEXTAREA -->
          <textarea
            v-model="text"
            :maxlength="maxLength"
            rows="4"
            class="w-full resize-y bg-transparent outline-none text-sm text-gray-800 dark:text-gray-100 placeholder-gray-400 p-3 pr-16"
            :placeholder="t('enterIntroduction') + '...'"
          />

          <!-- COUNTER -->
          <span
            class="absolute bottom-2 right-3 text-xs text-gray-400 dark:text-gray-500"
          >
            {{ text.length }}/{{ maxLength }} {{ t("character")}}
          </span>
        </div>
      </div>
    </div>

    <!-- ACTION BUTTON -->
    <div class="absolute flex w-1/2 gap-3 bottom-6 right-2">
      <button
        class="flex-1 border hover:bg-gray-50 dark:bg-gray-700 dark:hover:bg-slate-600 dark:border-slate-700 dark:text-slate-300 py-2 rounded cursor-pointer"
        @click="goPage('friendProfile')"
      >
        {{ t("info") }}
      </button>
      <button
        class="flex-1 hover:bg-blue-500 bg-blue-600 text-white py-2 rounded cursor-pointer"
        @click="sendAddFriendRequest"
      >
        {{ t("addFriend") }}
      </button>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useTranslate } from "@/composables/useTranslate";
import { useFriendshipStore } from "@/stores/friendship.storage";
import { useUserStore } from "@/stores/user.storage";
import { UserType } from "@/types/entities";
import { inject, ref } from "vue";

const props = defineProps<{
  user: UserType;
  goPage: (value: any) => void;
}>();
const { t } = useTranslate();
const userStorage = useUserStore()
const friendshipStorage = useFriendshipStore()

const defaultText = `Xin chào, mình là ${userStorage.user?.username}. Mình biết bạn qua nhóm chung. Kết bạn với mình nhé!`
const text = ref(defaultText);
const maxLength = 150;
const dismiss = inject<() => void>("modalDismiss")

const sendAddFriendRequest = async () => {
    const success = await friendshipStorage.sendAddFriendRequest({
        content: text.value,
        toId: props.user.id
    })

    success && dismiss?.()
}
</script>
