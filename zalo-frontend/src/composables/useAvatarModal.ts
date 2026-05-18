import { UserType, ConversationType } from "@/types/entities";
import { ref } from "vue";

export type AvatarModalType = 'user' | 'group';
export type GroupView = 'groupInfo' | 'member';

const isOpen = ref(false);
const modalType = ref<AvatarModalType>('user');
const user = ref<UserType | null>(null);
const conversation = ref<ConversationType | null>(null);
const groupView = ref<GroupView>('groupInfo');
const isMember = ref(false);

export function useAvatarModal() {
    const openUserModal = (u: UserType) => {
        user.value = u;
        modalType.value = 'user';
        isOpen.value = true;
    };

    const openGroupModal = (conv: ConversationType, member = false) => {
        conversation.value = conv;
        modalType.value = 'group';
        groupView.value = 'groupInfo';
        isMember.value = member;
        isOpen.value = true;
    };

    const closeModal = () => {
        isOpen.value = false;
        user.value = null;
        conversation.value = null;
        groupView.value = 'groupInfo';
    };

    const setGroupView = (view: GroupView) => {
        groupView.value = view;
    };

    return {
        isOpen,
        modalType,
        user,
        conversation,
        groupView,
        isMember,
        openUserModal,
        openGroupModal,
        closeModal,
        setGroupView,
    };
}
