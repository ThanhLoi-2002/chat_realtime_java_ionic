import { nextTick, Ref } from "vue"

export function useScroll() {
    const onScroll = async (scrollContainer: HTMLElement | null, callback: () => Promise<void>) => {
        const el = scrollContainer
        if (!el) return

        if (el.scrollTop <= 10) {
            await callback()
        }
    }

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