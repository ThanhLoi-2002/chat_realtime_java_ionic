<template>
    <form @submit="save" class="space-y-4">
        <ErrorInput :errors="errors" name="name" label="name" :define-field="defineField"
            :schema="subscriptionPackageSchema" placeholder="name" direction="vertical" />

        <ErrorInput :errors="errors" name="price" label="price" :define-field="defineField"
            :schema="subscriptionPackageSchema" placeholder="price" direction="vertical" />

        <ErrorInput :errors="errors" name="description" label="description" :define-field="defineField"
            :schema="subscriptionPackageSchema" placeholder="description" direction="vertical" />

        <div class="grid grid-cols-2 gap-4">
            <ErrorSelect label="level" name="level" :defineField="defineField" :errors="errors"
                :schema="subscriptionPackageSchema" placeholder="selectLevel"
                :options="enumToOptions(SubScriptionPackageLevelEnum)" direction="vertical" />

            <ErrorSelect label="category" name="category" :defineField="defineField" :errors="errors"
                :schema="subscriptionPackageSchema" placeholder="selectCategory"
                :options="enumToOptions(SubscriptionCategoryEnum)" direction="vertical" />
        </div>

        <div class="grid grid-cols-2 gap-4">
            <ErrorSelect label="targetType" name="targetType" :defineField="defineField" :errors="errors"
                :schema="subscriptionPackageSchema" placeholder="selectTargetType"
                :options="enumToOptions(TargetTypeEnum)" direction="vertical" />

            <ErrorSelect label="billingCycle" name="billingCycle" :defineField="defineField" :errors="errors"
                :schema="subscriptionPackageSchema" placeholder="selectBillingCycle"
                :options="enumToOptions(BillingCycleEnum)" direction="vertical" />
        </div>

        <FeatureInput name="features" label="features" :define-field="defineField" :errors="errors"
            direction="vertical" />

        <GiftInput name="gifts" label="Quà tặng kèm theo" :define-field="defineField" :errors="errors"
            direction="vertical" />

        <div :class="[style.border.secondary, 'flex justify-end gap-3 pt-4 border-t']">
            <BaseButton type="button" label="cancel" @click="emit('close')" :disabled="isLoading"
                customClass="bg-red-500 hover:bg-red-500/90 text-slate-200 px-2 py-0.5 rounded-sm" />
            <BaseButton type="submit" :label="selectedItem?.id ? 'update' : 'create'"
                customClass="bg-blue-500 hover:bg-blue-500/90 text-slate-200 px-2 py-0.5 rounded-sm" />
        </div>
    </form>
</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import BaseButton from '@/components/Shared/Button/BaseButton.vue';
import ErrorInput from '@/components/Shared/Form/ErrorInput.vue';
import ErrorSelect from '@/components/Shared/Form/ErrorSelect.vue';
import FeatureInput from '@/components/Shared/Form/FeatureInput.vue';
import GiftInput from '@/components/Shared/Form/GiftInput.vue';
import { SubscriptionPackageFormType, subscriptionPackageSchema } from '@/schema/Admin/subscriptionPackage.schema';
import { useSubscriptionPackageStore } from '@/stores/App/subscriptionPackage.storage';
import { SubscriptionPackageType } from '@/types/entities';
import { BillingCycleEnum, SubscriptionCategoryEnum, SubScriptionPackageLevelEnum, TargetTypeEnum } from '@/types/enum';
import { enumToOptions } from '@/utils/helper';
import { toTypedSchema } from '@vee-validate/yup';
import { useForm } from 'vee-validate';
import { ref, watch } from 'vue';

const props = defineProps<{
    selectedItem?: SubscriptionPackageType | undefined
}>()

const emit = defineEmits<{
    (e: 'close'): void
    (e: 'fetch-list'): void
}>()
const isLoading = ref(false)
const subscriptionPackageStor = useSubscriptionPackageStore()

const { handleSubmit, errors, defineField, setValues } = useForm({
    validationSchema: toTypedSchema(subscriptionPackageSchema),
    validateOnMount: false
});

const save = handleSubmit(async (values: SubscriptionPackageFormType) => {
    isLoading.value = true
    try {
        if (props.selectedItem) {
            await subscriptionPackageStor.update(props.selectedItem.id, values)
        } else {
            await subscriptionPackageStor.create(values)
        }
        emit('close')
        emit('fetch-list')
    } catch (error) {
        console.error(error)
    } finally {
        isLoading.value = false
    }
}, (e) => console.log(e.errors))

// Theo dõi dữ liệu được truyền vào form
watch(
    () => props.selectedItem,
    (item) => {
        if (item) {
            setValues({
                ...item
            });
        }
    },
    { immediate: true }
);
</script>