import { ref, onMounted, onUnmounted } from "vue"
import { isPlatform } from '@ionic/vue';
import { Device } from '@capacitor/device';
import { getPlatforms } from '@ionic/vue';

export function useDevice() {

  const isMobile = ref(false)
  const platforms = getPlatforms();

  function check() {
    isMobile.value = window.innerWidth < 768
  }

  const checkDevice = () => {
    if (isPlatform('ios')) {
      console.log('Đây là thiết bị iOS');
    } else if (isPlatform('android')) {
      console.log('Đây là thiết bị Android');
    }

    if (isPlatform('capacitor')) {
      console.log('Đang chạy trong Native App (Hybrid)');
    } else {
      console.log('Đang chạy trên Trình duyệt (Web)');
    }
  };

  const isSmartDevice = () => {
    // Trả về true nếu chạy trên App cài đặt (Android/iOS App)
    // Trả về false nếu chạy trên Chrome/Safari/Edge...
    return isPlatform('hybrid');
  };

  const logDeviceInfo = async () => {
    const info = await Device.getInfo();
    console.log(info);
  };

  onMounted(() => {
    check()
    window.addEventListener("resize", check)
  })

  onUnmounted(() => {
    window.removeEventListener("resize", check)
  })

  return {
    isMobile, checkDevice, platforms, logDeviceInfo, isSmartDevice
  }
}