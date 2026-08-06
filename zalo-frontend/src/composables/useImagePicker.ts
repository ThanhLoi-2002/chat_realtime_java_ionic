import { MINIO_URL } from "@/utils/constant"

export function useImagePicker() {
    const handlePickerUrl = (url: string) => {
        return url.startsWith('blob') ? url : `${MINIO_URL}/${url}`
    }

    return {
        handlePickerUrl
    }
}