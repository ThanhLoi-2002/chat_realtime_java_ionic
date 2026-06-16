<template>
    <Modal ref="modalRef" :title="title" @close="closeModal">
        <template v-if="modalType === 'user' && user">
            <FriendProfileUI :user="user" />
        </template>
        <template v-else-if="modalType === 'group' && conversation">
            <transition name="slide">
                <GroupProfile
                    v-if="groupView === 'groupInfo'"
                    :conversation="conversation"
                    :is-member="isMember"
                    @update:view="setGroupView($event)"
                />
                <Member
                    v-else-if="groupView === 'member'"
                    :is-show-back-button="true"
                    :members="conversation.members"
                    @back="setGroupView('groupInfo')"
                />
            </transition>
        </template>
    </Modal>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, ref, watch } from "vue";
import { useAvatarModal } from "@/composables/useAvatarModal";
import { useTranslate } from "@/composables/useTranslate";
import Modal from "@/components/Shared/Modal/Modal.vue";
import FriendProfileUI from "@/views/Chat/component/FriendProfileUI.vue";

const GroupProfile = defineAsyncComponent(() => import("@/views/Chat/component/GroupProfile.vue"));
const Member = defineAsyncComponent(() => import("@/views/Chat/Info/components/Member.vue"));

const { isOpen, modalType, user, conversation, groupView, isMember, closeModal, setGroupView } = useAvatarModal();
const { t } = useTranslate();

const modalRef = ref<InstanceType<typeof Modal> | null>(null);

const title = computed(() => {
    if (modalType.value === "user") return t("profile");
    return t("groupProfile");
});

watch(isOpen, (val, oldVal) => {
    if (val === oldVal) return;
    if (val) {
        // Đợi DOM cập nhật nội dung slot rồi mới present
        requestAnimationFrame(() => {
            modalRef.value?.present();
        });
    } else {
        modalRef.value?.dismiss();
    }
});
</script>
