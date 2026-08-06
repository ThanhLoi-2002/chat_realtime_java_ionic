<template>
    <div class="space-y-6">
        <!-- Ảnh bìa -->
        <div class="flex items-start justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40 pt-2']">Ảnh bìa</span>
            <div class="flex-1">
                <CoverPicker :cover="oa.cover" @preview="oa.cover = $event" @update:cover="avatarFile = $event" size="w-32 h-20"/>
            </div>
        </div>

        <!-- Ảnh đại diện -->
        <div class="flex items-start justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40 pt-2']">Ảnh đại diện</span>
            <div class="flex-1">
                <AvatarPicker :avatar="oa.avatar" @preview="oa.avatar = $event" @update:avatar="avatarFile = $event" size="size-15"/>
            </div>
        </div>

        <!-- Tên Official Account -->
        <div class="flex items-center justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40']">Tên Official Account</span>
            <div class="flex-1">
                <input type="text" v-model="oa.name"
                    :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'w-full px-3 py-2 border rounded text-sm focus:ring-1 focus:ring-slate-500 focus:outline-none font-medium']" />
            </div>
        </div>

        <!-- Thông tin giới thiệu -->
        <div class="flex items-start justify-between">
            <span :class="[oaStyle.text.primary, 'text-sm w-40 pt-2']">Thông tin giới thiệu</span>
            <div class="flex-1">
                <textarea v-model="oa.description" rows="3"
                    :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'w-full px-3 py-2 border rounded text-sm focus:ring-1 focus:ring-slate-500 focus:outline-none']"></textarea>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import AvatarPicker from '@/components/Shared/ImagePicker/AvatarPicker.vue';
import CoverPicker from '@/components/Shared/ImagePicker/CoverPicker.vue';
import { useTranslate } from '@/composables/useTranslate';
import { OaType } from '@/types/entities';
import { ref } from 'vue';

const oa = defineModel<OaType>('oa', { required: true })
const { t } = useTranslate()
const avatarFile = ref<File | null>(null)
// truyền thêm emit ra ngoài cho avatar

</script>