<template>
    <img :class="[
        customClass,
        size ? size : 'size-12',
        'rounded-full',
        'cursor-pointer'
    ]" :src="user?.avatar?.url || RANDOM_AVATAR" :onClick="handleClick" />
</template>
<script setup lang="ts">
import { useAvatarModal } from '@/composables/useAvatarModal';
import { UserType } from '@/types/entities';
import { RANDOM_AVATAR } from '@/utils/constant';

const props = defineProps<{
    user?: UserType
    size?: string
    isDisabled?: boolean
    customClass?: string
}>()

const { openUserModal } = useAvatarModal()

const handleClick = () => {
    if (!props.isDisabled && props.user) {
        openUserModal(props.user)
    }
}
</script>
