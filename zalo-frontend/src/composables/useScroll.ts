import { nextTick, Ref } from "vue"

export function useScroll() {
    // const onScroll = async (scrollContainer: HTMLElement | null, callback: () => Promise<void>) => {
    //     const el = scrollContainer
    //     if (!el) return

    //     if (el.scrollTop <= 10) {
    //         await callback()
    //     }
    // }

    const onScroll = async (scrollContainer: HTMLElement | null, callback: () => Promise<void>) => {
        const el = scrollContainer;
        if (!el) return; // Tránh gọi trùng lặp khi đang load

        if (el.scrollTop <= 10) {
            // 1. Lưu lại chiều cao và vị trí hiện tại
            const previousScrollHeight = el.scrollHeight;
            const previousScrollTop = el.scrollTop;

            await callback();

            // 2. Chờ DOM render xong các item mới
            nextTick(() => {
                // 3. Tính toán độ chênh lệch chiều cao
                const newScrollHeight = el.scrollHeight;
                const heightDifference = newScrollHeight - previousScrollHeight;

                // 4. Đẩy thanh cuộn xuống đúng vị trí cũ so với các item cũ
                el.scrollTop = previousScrollTop + heightDifference;
            });
        }
    };

    const scrollToBottom = async (scrollContainer: HTMLElement | null, smooth: boolean) => {
        await nextTick() // 🔥 đợi DOM render xong

        const el = scrollContainer
        if (!el) return

        const maxScroll = el.scrollHeight - el.clientHeight

        if (smooth) {
            el.scrollTo({ top: maxScroll, behavior: 'smooth' })

            setTimeout(() => {
                el.scrollTop = el.scrollHeight
            }, 500)
        } else {
            el.scrollTop = el.scrollHeight
        }
    }
    return {
        onScroll, scrollToBottom
    }
}