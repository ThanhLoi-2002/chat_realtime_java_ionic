import { useUserStore } from "@/stores/user.storage"
import { FriendshipType } from "@/types/entities"

export function useFriendship() {
    const userStorage = useUserStore()

    const getOther = (f: FriendshipType) => {
        return userStorage.user?.id == f.user1.id ? f.user2 : f.user1
    }

    return {
        getOther
    }
}