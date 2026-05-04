import { ToastType } from '@/types/common';
import { toastController } from '@ionic/vue';
import { alertCircleOutline } from 'ionicons/icons';

export const useToast = () => {
  const showZaloToast = async (options: ToastType) => {
    const { message, color = 'success', duration = 2000, position = 'middle' } = options
    const toast = await toastController.create({
      message: message,
      duration: duration,
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

  return { showZaloToast };
};