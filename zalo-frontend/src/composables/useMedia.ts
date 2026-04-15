import { MediaType } from "@/types/entities";
import { useDateTime } from "./useDateTime";

export function useMedia() {
    const { formatDate } = useDateTime()

    const groupedMediaByTime = (filtereds: any[]) => {
        const map: Record<string, any[]> = {};

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

    const isVideo = (url: string) => {
        if (!url) return false;
        // Kiểm tra các đuôi file video phổ biến
        const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov', '.m4v'];
        return videoExtensions.some(ext => url.toLowerCase().endsWith(ext)) || url.includes('/video/upload/');
    };

    return {
        groupedMediaByTime, isVideo
    }
}