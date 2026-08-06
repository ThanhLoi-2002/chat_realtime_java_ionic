<template>
    <div class="space-y-6">
        <!-- Ảnh bìa -->
        <div class="flex items-center justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40 pt-2']">{{ t('cover') }}</span>
            <div class="flex-1">
                <CoverPicker :cover="oa.cover" @preview="oa.cover = $event"
                    @update:cover="emit('update:coverFile', $event)" size="w-32 h-20" />
            </div>
        </div>

        <!-- Ảnh đại diện -->
        <div class="flex items-center justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40 pt-2']">{{ t('avatar') }}</span>
            <div class="flex-1">
                <AvatarPicker :avatar="oa.avatar" @preview="oa.avatar = $event"
                    @update:avatar="emit('update:avatarFile', $event)" size="size-15" />
            </div>
        </div>

        <!-- Tên Official Account -->
        <div class="flex items-center justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40']">{{ t('oaName') }}</span>
            <div class="flex-1">
                <input type="text" v-model="oa.name"
                    :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'w-full px-3 py-2 border rounded text-sm focus:ring-1 focus:ring-slate-500 focus:outline-none font-medium']" />
            </div>
        </div>

        <!-- Thông tin giới thiệu -->
        <div class="flex items-center justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40']">{{ t('description') }}</span>
            <div class="flex-1 flex items-center gap-2">
                <textarea v-model="oa.description" rows="3"
                    :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'w-full px-3 py-2 border rounded text-sm focus:ring-1 focus:ring-slate-500 focus:outline-none']"></textarea>
                <Switch v-model="oa.display.showDescription" size="w-9 h-5 after:h-4 after:w-4" />
            </div>
        </div>

        <div class="flex items-center justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40']">{{ t('address') }}</span>
            <div class="flex-1 flex items-center gap-2">
                <AddressSelect v-model:province="oa.province" v-model:district="oa.district"
                    v-model:address="oa.address" />
                <Switch v-model="oa.display.showAddress" size="w-9 h-5 after:h-4 after:w-4" />
            </div>
        </div>
        <div class="flex items-center justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40']">{{ t('phoneNumber') }}</span>
            <div class="flex-1 flex items-center gap-2">
                <input type="text" v-model="oa.phone"
                    :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'w-full px-3 py-2 border rounded text-sm focus:ring-1 focus:ring-slate-500 focus:outline-none']" />
                <Switch v-model="oa.display.showPhone" size="w-9 h-5 after:h-4 after:w-4" />
            </div>
        </div>
        <div class="flex items-center justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40']">{{ t('workingHours') }}</span>
            <div class="flex-1 flex items-center gap-2">
                <WorkingHoursSelector v-model:start-hour="oa.startHour" v-model:end-hour="oa.endHour"
                    v-model:is-whole-day="oa.isWholeDay" />
                <Switch v-model="oa.display.showWorkingHours" size="w-9 h-5 after:h-4 after:w-4" />
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import WorkingHoursSelector from '@/components/OA/Select/WorkingHoursSelector.vue';
import AvatarPicker from '@/components/Shared/ImagePicker/AvatarPicker.vue';
import CoverPicker from '@/components/Shared/ImagePicker/CoverPicker.vue';
import AddressSelect from '@/components/Shared/Select/AddressSelect.vue';
import Switch from '@/components/Shared/Switch/Switch.vue';
import { useTranslate } from '@/composables/useTranslate';
import { OaType } from '@/types/entities';

const oa = defineModel<OaType>('oa', { required: true })
const { t } = useTranslate()
// truyền thêm emit ra ngoài cho avatar
const emit = defineEmits<{
    (e: 'update:coverFile', file: File): void
    (e: 'update:avatarFile', file: File): void
}>()
</script>