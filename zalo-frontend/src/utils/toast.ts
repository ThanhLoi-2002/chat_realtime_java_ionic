import { useTranslate } from '@/composables/useTranslate'
import { ToastType } from '@/types/common'
import { toastController } from '@ionic/vue'
import { addIcons } from 'ionicons'
import {
    checkmarkCircle,
    closeCircle,
    alertCircle,
    informationCircle,
    close,
    alertCircleOutline
} from 'ionicons/icons'

export const toast = async (options: ToastType) => {
    const { message, color = 'success', duration = 2000, position = 'middle' } = options
    const { t } = useTranslate()
    const toast = await toastController.create({
        message: t(message),
        duration,
        // color,
        position,
        cssClass: 'zalo-style-toast', // Class tùy chỉnh trong global css
        buttons: [
            {
                side: 'start',
                icon: alertCircleOutline, // Icon dấu chấm than giống hình
            },
        ],
        // icon: getIcon(color),
        // cssClass: 'app-toast',
        // buttons: [
        //     {
        //         icon: close,
        //         role: 'cancel'
        //     }
        // ]
    })

    await toast.present()
}

export const showZaloToast = async (options: ToastType) => {
    const { message, color = 'success', duration = 2000000, position = 'middle' } = options
    const toast = await toastController.create({
        message,
        duration,
        position,
        cssClass: 'zalo-style-toast', // Class tùy chỉnh trong global css
        buttons: [
            {
                side: 'start',
                icon: alertCircleOutline, // Icon dấu chấm than giống hình
            },
        ],
    });

    await toast.present();
};

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