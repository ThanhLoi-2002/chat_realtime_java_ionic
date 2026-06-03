// @/composables/useClickOutside.ts
import { onBeforeUnmount, onMounted, type Ref } from "vue";

export function useClickOutside(
    targetRef: Ref<HTMLElement | null>,
    callback: () => void
) {
    const handler = (event: MouseEvent) => {
        // Kiểm tra xem phần tử có tồn tại và cú click có nằm ngoài phần tử đó không
        if (targetRef.value && !targetRef.value.contains(event.target as Node)) {
            callback();
        }
    };

    onMounted(() => {
        // Sử dụng sự kiện click hoặc click (click thường nhạy và mượt hơn)
        window.addEventListener("click", handler);
    });

    onBeforeUnmount(() => {
        // Hủy lắng nghe khi component bị unmount để tránh rò rỉ bộ nhớ
        window.removeEventListener("click", handler);
    });
}