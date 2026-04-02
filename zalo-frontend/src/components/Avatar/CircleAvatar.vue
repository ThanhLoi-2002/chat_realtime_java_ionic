<template>
    <img :class="[
        customClass,
        size ? size : 'size-12',
        'rounded-full',
        'cursor-pointer'
    ]" :src="user?.avatar?.url || RANDOM_AVATAR" :onClick="() => !isDisabled && profileModal?.present()" />

    <Modal ref="profileModal" :title="t('profile')">
        <FriendProfileUI :user="user"/>
    </Modal>
</template>
<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
import { UserType } from '@/types/entities';
import { RANDOM_AVATAR } from '@/utils/constant';
import FriendProfileUI from '@/views/Chat/component/FriendProfileUI.vue';
import { ref } from 'vue';
import Modal from '../Modal/Modal.vue';

defineProps<{
    user?: UserType
    size?: string
    isDisabled?: boolean
    customClass?: string
}>()

const { t } = useTranslate()
const profileModal = ref()
</script>