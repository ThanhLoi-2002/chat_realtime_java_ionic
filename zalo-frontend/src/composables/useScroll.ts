import { nextTick } from "vue"

export function useScroll() {

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

    const onScrollDown = async (scrollContainer: HTMLElement | null, callback: () => Promise<void>) => {
        const el = scrollContainer;
        if (!el) return;

        const { scrollTop, scrollHeight, clientHeight } = el;
        const distanceFromBottom = scrollHeight - scrollTop - clientHeight;

        if (distanceFromBottom <= 10) {
            // 1. Lưu scrollHeight cũ và scrollTop để duy trì vị trí sau khi thêm message mới
            const previousScrollHeight = el.scrollHeight;
            const previousScrollTop = el.scrollTop;

            await callback();

            // 2. Chờ DOM render xong các item mới
            nextTick(() => {
                const newScrollHeight = el.scrollHeight;
                const heightDifference = newScrollHeight - previousScrollHeight;

                // Khi load newer messages, chúng được thêm vào cuối.
                // Scroll sẽ tự động giữ nguyên vị trí ở dưới cùng
                // Vì các message mới thêm vào phía dưới nên scrollHeight tăng,
                // ta chỉ cần scrollTop tăng tương ứng = previousScrollTop (không đổi vị trí)
                el.scrollTop = previousScrollTop;
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

    const scrollToElement = async (scrollContainer: HTMLElement | null, elementId: string, smooth: boolean) => {
        const el = scrollContainer
        if (!el) return

        await nextTick()
        const target = document.getElementById(elementId)
        if (target) {
            const containerRect = el.getBoundingClientRect()
            const targetRect = target.getBoundingClientRect()
            const offset = targetRect.top - containerRect.top + el.scrollTop - containerRect.height / 2 + targetRect.height / 2

            if (smooth) {
                el.scrollTo({ top: offset, behavior: 'smooth' })
            } else {
                el.scrollTop = offset
            }
        }
    }

    return {
        onScroll, onScrollDown, scrollToBottom, scrollToElement
    }
}