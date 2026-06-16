<template>
    <div>
        <div class="relative">
            <div class="h-36 cursor-pointer">
                <img :src="user?.cover?.url ?? RANDOM_AVATAR" class="w-full h-full rounded-lg" />
            </div>

            <!-- Avatar center -->
            <div class="absolute left-1/2 -bottom-12 -translate-x-1/2">
                <div class="relative w-24 h-24">

                    <!-- Avatar -->
                    <CircleAvatar :user="user!"
                        custom-class="w-24 h-24 rounded-full ring-2 ring-white dark:ring-slate-800 overflow-hidden bg-gray-300"
                        :is-disabled="true" />
                </div>
            </div>
        </div>

        <!-- BODY -->
        <div class="pt-10 pb-4 px-2 dark:bg-gray-800 overflow-auto h-[75%]">
            <!-- Name -->
            <div class="flex items-center justify-center gap-2">
                <h2 class="text-lg font-semibold dark:text-slate-300">
                    {{ user?.username }}
                </h2>
            </div>

            <!-- OPTIONS -->
            <div class="mt-5 border-t border-gray-700 pt-4 space-y-1 text-sm">
                <div v-for="(item, index) in items" :key="index" @click="item.onClick"
                    :class="item.class ?? 'dark:text-slate-200'"
                    class="flex items-center gap-3 cursor-pointer hover:bg-slate-200 dark:hover:bg-slate-700 p-2 rounded-sm">
                    <i :class="item.icon"></i>
                    <span>{{ t(item.title) }}</span>
                </div>
            </div>
        </div>

        <Modal ref="profileModal" :title="t('profile')">
            <FriendProfileUI :user="user" />
        </Modal>
    </div>
</template>
<script setup lang="ts">
import Modal from '@/components/Shared/Modal/Modal.vue';
import { useConfirmStore } from '@/composables/useConfirm';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/App/conversation.storage';
import { MemberType } from '@/types/entities';
import { MemberRoleEnum } from '@/types/enum';
import { RANDOM_AVATAR } from '@/utils/constant';
import FriendProfileUI from '@/views/Chat/component/FriendProfileUI.vue';
import { computed, inject, ref } from 'vue';

const props = defineProps<{
    user: MemberType
    setSelectedUser: (user?: MemberType) => void
    closeMemberManagementModal: () => void
}>()

const { t } = useTranslate()
const profileModal = ref()
const convStorage = useConversationStore()
const confirmStore = useConfirmStore();

const dismiss = inject<() => void>("modalDismiss")

const items = computed(() => {
    // Giả sử props.member.role hoặc một biến tương đương lưu role hiện tại của user đó
    const isSilverKey = props.user.role === MemberRoleEnum.SILVER_KEY;

    return [
        {
            icon: 'fa fa-users',
            title: 'profile',
            onClick: () => { profileModal.value.present() }
        },
        // Nút Giao phó Golden Key (Chuyển nhượng trưởng nhóm)
        {
            icon: 'fa fa-key text-yellow-400',
            title: 'appointGroupLeader',
            onClick: () => {
                confirmStore.open({
                    title: t('appointGroupLeader'),
                    message: t('appointGroupLeader'),
                    onOk: () => {
                        ordain(MemberRoleEnum.GOLDEN_KEY)
                    }
                });
            }
        },
        // Nút Phong / Tước Silver Key
        {
            icon: isSilverKey ? 'fab fa-rev' : 'fa fa-key',
            title: isSilverKey ? 'revokeGroupDeputy' : 'appointGroupDeputy',
            onClick: () => {
                if (isSilverKey) {
                    confirmStore.open({
                        title: t('revokeGroupDeputy'),
                        message: t('revokeGroupDeputy'),
                        onOk: async () => {
                            convStorage.revokeSilverKey(props.user.id)
                            dismiss?.()
                            props.closeMemberManagementModal()
                        }
                    });
                } else {
                    confirmStore.open({
                        title: t('appointGroupDeputy'),
                        message: t('appointGroupDeputy'),
                        onOk: () => {
                            ordain(MemberRoleEnum.SILVER_KEY)
                        }
                    });
                }
            }
        },
        {
            icon: 'fa fa-warning',
            title: 'kickMember',
            class: 'text-red-400',
            onClick: () => {
                confirmStore.open({
                    title: t('kickMember'),
                    message: t('kickMember'),
                    onOk: async () => {
                        await convStorage.kickMember(props.user.id)
                        props.setSelectedUser()
                        dismiss?.()
                        props.closeMemberManagementModal()
                    }
                });
            }
        },
    ];
});

const ordain = (level: MemberRoleEnum.GOLDEN_KEY | MemberRoleEnum.SILVER_KEY) => {
    if (level == MemberRoleEnum.GOLDEN_KEY) {
        convStorage.transferGoldenKey(props.user.id)
    } else {
        convStorage.ordainSilverKey(props.user.id)
    }
    dismiss?.()
    props.closeMemberManagementModal()
}
</script>