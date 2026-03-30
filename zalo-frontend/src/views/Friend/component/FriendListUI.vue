<template lang="">
  <!-- CONTENT -->
  <div class="flex-1 flex flex-col h-full">
    <!-- TOP BAR (KHÔNG SCROLL) -->
    <div class="p-3 md:p-4 pb-2">
      <div class="flex flex-col md:flex-row gap-2 md:gap-3">
        <div
          class="flex-1 bg-gray-100 dark:bg-gray-800 rounded-lg px-3 py-2 flex items-center gap-2"
        >
          <i class="fa fa-search text-gray-500"></i>
          <input
            v-model="keyword"
            placeholder="Tìm bạn"
            class="bg-transparent outline-none text-sm w-full text-gray-900 dark:text-white placeholder-gray-400"
          />
        </div>
      </div>
    </div>

    <!-- LIST (SCROLL) -->
    <div class="flex-1 overflow-y-auto px-3 md:px-4 pb-4">
      <div v-for="(group, letter) in groupedFriends" :key="letter">
        <!-- LETTER -->
        <div
          class="text-gray-500 dark:text-gray-400 text-xs md:text-sm mb-2 mt-4"
        >
          {{ letter }}
        </div>

        <!-- USERS -->
        <div
          v-for="u in group"
          :key="u.id"
          class="flex items-center justify-between py-2 rounded-lg cursor-pointer transition-all duration-200 hover:bg-gray-100 dark:hover:bg-gray-800 hover:scale-[1.01]"
        >
          <div class="flex items-center gap-3">
            <img
              :src="u.avatar.url"
              class="w-9 h-9 md:w-10 md:h-10 rounded-full object-cover"
            />

            <span class="text-sm md:text-base font-medium">
              {{ u.username }}
            </span>
          </div>

          <div class="relative">
            <button
              class="p-2 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition"
              @click.stop="toggleMenu(u.id)"
            >
              <i class="fa-solid fa-ellipsis text-sm"></i>
            </button>

            <!-- DROPDOWN -->
            <div
              v-if="openMenuId === u.id"
              class="absolute right-0 mt-1 w-32 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg shadow-lg z-20"
            >
              <div
                class="px-3 py-2 text-sm cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700 text-red-500"
                @click="confirmDelete(u)"
              >
                {{ t("unfriend") }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <ConfirmModal
  v-model:showConfirm="showConfirmDelete"
  :onOk="handleDeleteFriend"
  :message="t('confirmUnfriend')"
  :header="t('unfriend')"
/>
</template>
<script setup lang="ts">
import { useTranslate } from "@/composables/useTranslate";
import { useFriendshipStore } from "@/stores/friendship.storage";
import { UserType } from "@/types/entities";
import { normalizeText } from "@/utils/helper";
import { computed, onMounted, ref, watch } from "vue";

const { t } = useTranslate();
const friendshipStorage = useFriendshipStore();
const keyword = ref("");

// mock data
const friends = computed(() => friendshipStorage.friends)

const openMenuId = ref<number | null>(null)
const showConfirmDelete = ref(false)
const selectedUser = ref<UserType | null>(null)

/* toggle menu */
const toggleMenu = (id: number) => {
  openMenuId.value = openMenuId.value === id ? null : id
}

/* mở confirm */
const confirmDelete = (user: UserType) => {
  selectedUser.value = user
  showConfirmDelete.value = true
  openMenuId.value = null
}

/* xoá bạn */
const handleDeleteFriend = async () => {
  if (!selectedUser.value) return

  await friendshipStorage.unfriend(selectedUser.value.id)

  // update UI local (optional nếu store chưa auto update)
  // friends.value = friends.value.filter(
  //   f => f.id !== selectedUser.value?.id
  // )

  selectedUser.value = null
}

// filter
const filtered = computed(() =>
  friends.value.filter((f) =>
    normalizeText(f.username)
      .toLowerCase()
      .includes(normalizeText(keyword.value).toLowerCase())
  ),
);

const groupedFriends = computed(() => {
  const groups: any = {};

  filtered.value.forEach((u) => {
    const letter = u.username.charAt(0).toUpperCase();
    if (!groups[letter]) groups[letter] = [];
    groups[letter].push(u);
  });

  return groups;
});

onMounted(() => {
  friendshipStorage.getFriends();
});
</script>
