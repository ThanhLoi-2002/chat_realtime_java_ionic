<script setup lang="ts">
import { onMounted } from "vue" // Bỏ ref nếu không dùng đến
import { IonButton } from "@ionic/vue";
import { useTranslate } from "@/composables/useTranslate";
import { oaStyle } from "@/assets/tailwindcss";
import { useRoute } from "vue-router";
import { useForm } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/yup';
import { useAdminUserStore } from "@/stores/Admin/user.storage";
import { userSchema } from "@/schema/Admin/user.schema";
import ErrorInput from "@/components/Shared/Form/ErrorInput.vue";

const { t } = useTranslate()
const route = useRoute()
const userStor = useAdminUserStore()
const { id } = route.query

// 1. Dùng defineForm thay vì tự quản lý ref thủ công
const { handleSubmit, errors, setValues, defineField } = useForm({
    validationSchema: toTypedSchema(userSchema),
});

const save = handleSubmit(async (values) => {
    console.log("Dữ liệu đã validate:", values)
});

onMounted(() => {
    const user = userStor.list.content.find(i => i.id == Number(id))
    if (user) {
        // 3. Dùng setValues để điền dữ liệu vào form thay vì gán trực tiếp vào ref
        setValues({
            username: user.username,
            phone: user.phone
        });
    }
})
</script>

<template>
    <div :class="[oaStyle.bg.primary, 'min-h-screen']">
        <back-button />
        <div :class="[oaStyle.bg.primary, oaStyle.border.primary, 'border rounded-lg shadow p-6']">
            <form @submit="save">
                <div class="space-y-5">
                    <error-input :errors="errors" name="username" label="username" :define-field="defineField"/>
                    <error-input :errors="errors" name="phone" label="phone" :define-field="defineField"/>
                </div>

                <div class="mt-6">
                    <ion-button type="submit" class="py-2 rounded-lg text-white">
                        <i class="fas fa-save mr-2" /> {{ t("save") }}
                    </ion-button>
                </div>
            </form>
        </div>
    </div>
</template>