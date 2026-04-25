import { useClassificationCardStore } from "@/stores/classificationCard.storage"

export function useClassCard() {
    const classCardStorage = useClassificationCardStore()

    const getClassCard = (convId?: number) => {
        if(!convId) return null
        return classCardStorage.cards.find(card =>
            card.conversationIds && card.conversationIds.includes(convId)
        );
    }

    return {
        getClassCard
    }
}