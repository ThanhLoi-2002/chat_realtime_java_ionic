import { useTranslate } from '@/composables/useTranslate'
import { toastController } from '@ionic/vue'
import { addIcons } from 'ionicons'
import {
    checkmarkCircle,
    closeCircle,
    alertCircle,
    informationCircle,
    close
} from 'ionicons/icons'

interface ToastType {
    message: string
    color?: 'success' | 'danger' | 'warning' | 'primary'
    duration?: number
    position?: "top" | "bottom" | "middle" | undefined
}
export const toast = async (options: ToastType) => {
    const { message, color = 'success', duration = 2000, position = 'top' } = options
    const { t } = useTranslate()
    const toast = await toastController.create({
        message: t(message),
        duration,
        color,
        position,
        icon: getIcon(color),
        cssClass: 'app-toast',
        buttons: [
            {
                icon: close,
                role: 'cancel'
            }
        ]
    })

    await toast.present()
}

addIcons({
    checkmarkCircle,
    closeCircle,
    alertCircle,
    informationCircle,
    close
})

function getIcon(color: string) {
    switch (color) {
        case 'success':
            return 'checkmark-circle'
        case 'danger':
            return 'close-circle'
        case 'warning':
            return 'alert-circle'
        default:
            return 'information-circle'
    }
}