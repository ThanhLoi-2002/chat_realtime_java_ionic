<script setup lang="ts">
import { ref, onMounted } from "vue";

const roles = ref([]);
const selectedRoles = ref<number[]>([]);

const loadData = async () => {
  roles.value = await api.getRoles();

  selectedRoles.value =
    await api.getUserRoles(userId);
};

const save = async () => {
  await api.assignRoles(userId, {
    roleIds: selectedRoles.value,
  });
};

onMounted(loadData);
</script>

<template>
  <div class="space-y-3">

    <label
      v-for="role in roles"
      :key="role.id"
      class="flex items-center gap-2"
    >
      <input
        type="checkbox"
        :value="role.id"
        v-model="selectedRoles"
      />

      {{ role.name }}
    </label>

    <button
      class="px-4 py-2 rounded bg-blue-500 text-white"
      @click="save"
    >
      Save
    </button>

  </div>
</template>