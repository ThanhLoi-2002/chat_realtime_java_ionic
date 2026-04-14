import { MediaType } from "@/types/entities";
import { useDateTime } from "./useDateTime";

export function useMedia() {
    const { formatDate } = useDateTime()
    
    const groupedMediaByTime = (filtereds: MediaType[]) => {
        const map: Record<string, MediaType[]> = {};

        filtereds.forEach((item) => {
            const ct = formatDate(item.ct);
            if (!map[ct]) map[ct] = [];
            map[ct].push(item);
        });

        return Object.keys(map).map((date) => ({
            date: date,
            items: map[date],
        }));
    };

    return {
        groupedMediaByTime
    }
}