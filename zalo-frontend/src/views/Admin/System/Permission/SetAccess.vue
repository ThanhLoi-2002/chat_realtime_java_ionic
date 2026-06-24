<template>

    <div class="rounded-lg mx-2">
        <BackButton/>
        <div :class="[oaStyle.border.primary, 'overflow-x-auto border rounded-sm my-2']">
            <table :class="[oaStyle.text.primary, 'w-full border-collapse text-left text-sm']">
                <thead>
                    <tr :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'border-b']">
                        <th
                            :class="[oaStyle.text.primary, oaStyle.border.primary, 'p-3 font-medium border-r text-center truncate w-1/4']">
                            {{ t('permission') }}
                        </th>
                        <th v-for="role in filteredRoles" :key="role.id"
                            :class="[oaStyle.text.primary, oaStyle.border.primary, 'p-3 font-medium border-r last:border-r-0 text-center']">
                            {{ t(role.name) }}
                        </th>
                    </tr>
                </thead>

                <tbody>
                    <tr v-for="item in features" :key="item"
                        :class="[oaStyle.bg.hover, oaStyle.border.primary, 'border-b last:border-b-0 transition-colors']">
                        <td :class="[oaStyle.border.primary, 'p-3 border-r']">
                            {{ item }}
                        </td>

                        <td v-for="role in filteredRoles" :key="role.id"
                            :class="[oaStyle.border.primary, 'p-3 border-r last:border-r-0 text-center']">
                            <input type="checkbox" :checked="hasPermission(item, role.id)"
                                @change="togglePermission(item, role.id)" :class="[
                                    oaStyle.border.primary,
                                    'w-4 h-4 cursor-pointer border bg-blue-500 checked:bg-blue-600'
                                ]" />
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div class="mt-4">
            <button @click="handleSave"
                class="inline-flex items-center gap-2 px-4 py-2 bg-green-500 hover:bg-green-600 text-white font-medium text-sm rounded shadow-sm transition-colors cursor-pointer">
                <i class="fas fa-save"></i>
                {{ t('save') }}
            </button>
        </div>
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import BackButton from '@/components/Shared/Button/BackButton.vue';
import { useTranslate } from '@/composables/useTranslate';
import { useAdminRoleStore } from '@/stores/Admin/role.storage';
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute()
const roleStor = useAdminRoleStore()
const { t } = useTranslate()

const {
    controllerCode,
    appType,
    moduleId
} = route.query

// Danh sách các Chức năng (Dòng)
const features = computed(() => {

    const permissionsByApp = roleStor.permissions.find(i => i.app == appType);

    const permiss = permissionsByApp?.modules.find(i => i.module == controllerCode)

    return permiss?.permissions ?? []
})
const selectedPermissions = ref<Record<string, number[]>>({})

const roles = computed(() => roleStor.roles)

const filteredRoles = computed(() => {
    return roles.value.filter(
        role => role.moduleId == (moduleId ?? 0)
    )
})

onMounted(async () => {
    roleStor.roles.length == 0 && await roleStor.getList()

    if (roleStor.permissions.length == 0) {
        await roleStor.getPermissions()
    }

    selectedPermissions.value = await roleStor.getAccess(features.value)
})

// Kiểm tra xem checkbox có được tích hay không
const hasPermission = (
    featureKey: string,
    roleId: number
): boolean => {
    return (
        selectedPermissions.value[featureKey]?.includes(roleId) ?? false
    )
}

// Xử lý khi thay đổi trạng thái checkbox
const togglePermission = (
    featureKey: string,
    roleId: number
) => {
    if (!selectedPermissions.value[featureKey]) {
        selectedPermissions.value[featureKey] = []
    }

    const list = selectedPermissions.value[featureKey]

    const index = list.indexOf(roleId)

    if (index >= 0) {
        list.splice(index, 1)
    } else {
        list.push(roleId)
    }
}

// Hàm xử lý khi bấm nút "Lưu"
const handleSave = () => {
    console.log(selectedPermissions.value)
    roleStor.saveSetPermission(selectedPermissions.value)
};

watch(
    features,
    (newFeatures) => {
        const data: Record<string, number[]> = {}

        newFeatures.forEach(feature => {
            data[feature] = []
        })

        selectedPermissions.value = data
    },
    { immediate: true }
)
</script>