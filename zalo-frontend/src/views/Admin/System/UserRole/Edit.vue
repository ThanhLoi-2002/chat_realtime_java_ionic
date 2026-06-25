<script setup lang="ts">
import { ref } from "vue"
import { IonButton } from "@ionic/vue"; // Thêm IonChip
import { useTranslate } from "@/composables/useTranslate";
import { oaStyle } from "@/assets/tailwindcss";
import { useAdminRoleStore } from "@/stores/Admin/role.storage";
import { SelectOptionType, SetUserRoleType } from "@/types/common";
import SelectChips from "@/components/Shared/Select/SelectChips.vue";
import { useRouter } from "vue-router";

const { t } = useTranslate()
const roleStor = useAdminRoleStore()
const router = useRouter()

// Dùng ref trực tiếp để Vue theo dõi được sự thay đổi bên trong mảng roles
const form = ref<SetUserRoleType>({
    ...roleStor.userRole,
    roleIds: roleStor.userRole.roleIds ?? [] // Đảm bảo luôn là mảng
})

// Danh sách tất cả các Role có trong hệ thống (Bạn thay bằng data từ API/Store của bạn)
const allRoles = ref<SelectOptionType[]>(roleStor.roles.map(i => ({ label: `${i.name} - ${i.appType}:${i.module}`, value: i.id })).sort((a, b) => a.label.localeCompare(b.label)))

const handleSubmit = async () => {
    const success = await roleStor.assignRoles(form.value)

    if(success) {
        router.back()
    }
}
</script>

<template>
    <div :class="[oaStyle.bg.primary, 'min-h-screen']">
        <back-button />
        <div :class="[oaStyle.bg.primary, oaStyle.border.primary, 'border rounded-lg shadow p-6']">
            <form @submit.prevent="handleSubmit">
                <div class="space-y-5">

                    <div class="flex justify-between items-center">
                        <label class="dark:text-white w-1/3">{{ t('username') }}</label>
                        <input :value="form.username" disabled
                            :class="[oaStyle.bg.primary, oaStyle.border.primary, oaStyle.text.primary, 'w-full px-3 py-2 rounded-md border']" />
                    </div>

                    <div class="flex justify-between items-center">
                        <label class="dark:text-white w-1/3">{{ t('phone') }}</label>
                        <input :value="form.phone" disabled
                            :class="[oaStyle.bg.primary, oaStyle.border.primary, oaStyle.text.primary, 'w-full px-3 py-2 rounded-md border']" />
                    </div>

                    <div class="flex justify-between items-center">
                        <label class="dark:text-white w-1/3">{{ t('roles') }}</label>

                        <select-chips v-model="form.roleIds" :options="allRoles" :placeholder="t('selectRoles')" />
                    </div>

                </div>

                <div class="mt-6">
                    <ion-button type="submit" class="py-2 rounded-lg text-white" :disabled="form.id == 0">
                        <i class="fas fa-save mr-2" /> {{ t("save") }}
                    </ion-button>
                </div>
            </form>
        </div>
    </div>
</template>