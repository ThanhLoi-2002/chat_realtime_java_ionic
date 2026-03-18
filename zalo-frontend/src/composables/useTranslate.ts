import { useLangStore } from "@/stores/lang.storage"

export const useTranslate = () => {
    const langStore = useLangStore()

    const t = (key: string) => {
        return langStore.langData[key] || key
    }

    return { t }
}