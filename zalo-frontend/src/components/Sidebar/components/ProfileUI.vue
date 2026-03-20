<template>
  <div class="dark:bg-slate-800 text-gray-700 dark:text-gray-200">

    <!-- Cover -->
    <div class="relative">
      <div class="h-36 cursor-pointer" @click="() => triggerFile('cover')">
        <img v-if="!isCoverLoading" :src="userStorage.user?.cover?.url ?? RANDOM_AVATAR"
          class="w-full h-full object-cover rounded-lg" />
        <div v-else class="w-full h-full object-cover rounded-lg bg-gray-500 flex items-center justify-center">
          <LoadingSpinner size="30px" />
        </div>

        <input ref="coverInput" type="file" accept="image/*" class="hidden" @change="(e) => onFileChange(e, 'cover')" />
      </div>

      <!-- Avatar center -->
      <div class="absolute left-1/2 -bottom-12 -translate-x-1/2">
        <div class="relative w-24 h-24">

          <!-- Avatar -->
          <CircleAvatar :url="userStorage.user?.avatar?.url ?? RANDOM_AVATAR" v-if="!isAvatarLoading"
            class="w-24 h-24 rounded-full ring-2 ring-white dark:ring-slate-800 overflow-hidden bg-gray-300" />

          <div v-else
            class="w-24 h-24 rounded-full ring-2 ring-white dark:ring-slate-800 overflow-hidden bg-gray-500 flex items-center justify-center">
            <LoadingSpinner size="30px" />
          </div>


          <!-- Nút upload (góc phải dưới) -->
          <button @click="() => triggerFile('avatar')" :disabled="isAvatarLoading"
            class="absolute bottom-0 right-0 w-6 h-6 rounded-full bg-blue-500 text-white flex items-center justify-center shadow cursor-pointer">
            <i class="fa fa-upload" />
          </button>

          <!-- Input file hidden -->
          <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="(e) => onFileChange(e, 'avatar')" />
        </div>
      </div>
    </div>

    <!-- Form -->
    <form class="mt-16 px-6 space-y-4" @submit.prevent="onSave">

      <!-- Username -->
      <div>
        <label class="text-sm text-gray-500">{{ t("username") }}</label>
        <input v-model="form.username" type="text"
          class="w-full mt-1 px-3 py-2 rounded-md border dark:bg-slate-700 dark:border-slate-600" />
      </div>

      <!-- Phone -->
      <div>
        <label class="text-sm text-gray-500">{{ t("phone") }}</label>
        <input v-model="form.phone" type="text"
          class="w-full mt-1 px-3 py-2 rounded-md border dark:bg-slate-700 dark:border-slate-600" disabled/>
      </div>

      <!-- Save button -->
      <button type="submit" class="w-full mt-4 py-2 rounded-md bg-blue-500 text-white hover:bg-blue-600 transition">
        {{ t("save") }}
      </button>

    </form>
  </div>
</template>

<script setup lang="ts">
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';
import { useTranslate } from '@/composables/useTranslate'
import { useUserStore } from '@/stores/user.storage';
import { SearchFriendPageType, SettingPageType } from '@/types/common';
import { RANDOM_AVATAR } from '@/utils/constant';
import { inject, reactive, ref } from 'vue';

const props = defineProps<{
  goPage?: (value: SettingPageType | SearchFriendPageType) => void
}>()

const dismiss = inject<() => void>("modalDismiss")

const { t } = useTranslate();
const userStorage = useUserStore()
const isAvatarLoading = ref(false)
const isCoverLoading = ref(false)

const form = reactive({
  username: userStorage.user!.username,
  phone: userStorage.user!.phone
})

const avatarInput = ref<HTMLInputElement | null>(null);
const coverInput = ref<HTMLInputElement | null>(null);

function onSave() {
  console.log("save", form)
}

const triggerFile = (type: "avatar" | "cover") => {
  if (isAvatarLoading.value || isCoverLoading.value) return
  if (type == 'avatar')
    avatarInput.value?.click();
  else coverInput.value?.click();
};

const onFileChange = async (e: Event, type: "avatar" | "cover") => {
  const target = e.target as HTMLInputElement;
  const file = target.files?.[0];

  if (!file) return;

  if (type == 'avatar') {
    isAvatarLoading.value = true
    await userStorage.uploadAvatar(file)
    isAvatarLoading.value = false
  } else {
    isCoverLoading.value = true
    await userStorage.uploadCover(file)
    isCoverLoading.value = false
  }
};
</script>