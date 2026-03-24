import { useMessageStore } from "@/stores/message.storage"
import { nextTick, Ref } from "vue"

export function useScroll() {
    const messageStorage = useMessageStore()

    const onScroll = async (scrollContainer: Ref<HTMLElement | null, HTMLElement | null>, callback: () => Promise<void>) => {
        const el = scrollContainer.value
        if (!el || messageStorage.isLoading || !messageStorage.hasMore) return

        if (el.scrollTop <= 10) {
            await loadMore(scrollContainer, callback)
        }
    }

    const loadMore = async (scrollContainer: Ref<HTMLElement | null, HTMLElement | null>, callback: () => Promise<void>) => {
        const el = scrollContainer.value
        if (!el) return

        if (!messageStorage.hasMore) return

        // lưu chiều cao cũ
        const oldHeight = el.scrollHeight

        // gọi API (page tiếp theo)
        await callback()

        // đợi DOM render xong
        await nextTick()

        // chiều cao mới
        const newHeight = el.scrollHeight

        // giữ vị trí scroll (không bị nhảy)
        el.scrollTop = newHeight - oldHeight
    }

    const scrollToBottom = async (scrollContainer: Ref<HTMLElement | null, HTMLElement | null>, smooth: boolean) => {
        await nextTick() // 🔥 đợi DOM render xong

        const el = scrollContainer.value
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
        onScroll, loadMore, scrollToBottom
    }
}