import { useConversationStore } from "@/stores/App/conversation.storage";
import { useConfirmStore } from "./useConfirm";
import { useTranslate } from "./useTranslate";
import { useMessageStore } from "@/stores/App/message.storage";
import { useUserStore } from "@/stores/App/user.storage";

export function useAuth() {
    const { t } = useTranslate();
    const convStorage = useConversationStore()
    const userStorage = useUserStore()
    const messStorage = useMessageStore()
    const confirmStore = useConfirmStore();

    const logout = () => {
        confirmStore.open({
            title: t('logout'),
            message: t('logout'),
            onOk: () => {
                userStorage.logout()
                messStorage.resetPagination()
                convStorage.reset()
                // dismiss?.()
            }
        });
    };

    return {
        logout
    };
}
