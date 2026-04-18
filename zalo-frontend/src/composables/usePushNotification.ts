import { useNotificationStore } from "@/stores/notification.storage";
import { useUserStore } from "@/stores/user.storage";
import { Capacitor } from "@capacitor/core";
import { PushNotifications } from "@capacitor/push-notifications";
import { useRouter } from "vue-router";
import { Device } from '@capacitor/device';
import { useConversationStore } from "@/stores/conversation.storage";

export function usePushNotification() {
    const router = useRouter();
    const notificationStorage = useNotificationStore()
    const userStorage = useUserStore()
    const convStorage = useConversationStore()

    const initPush = async () => {
        if (Capacitor.getPlatform() === 'web') return;

        // 1. Tạo Channel (Quan trọng để Rung/Tiếng)
        await PushNotifications.createChannel({
            id: 'tagMessage',
            name: 'Bạn vừa được nhắc đến trong 1 nhóm',
            importance: 5, // Cấp độ cao nhất: Hiện banner, Rung, Chuông
            sound: 'default',
            visibility: 1
        });

        // 2. Xin quyền
        let perm = await PushNotifications.checkPermissions();
        if (perm.receive !== 'granted') {
            perm = await PushNotifications.requestPermissions();
        }

        if (perm.receive === 'granted') {
            await PushNotifications.register();
        }

        // 3. Lấy Token để gửi cho Spring Boot
        PushNotifications.addListener('registration', async (token) => {
            if (userStorage.user) {
                const info = await Device.getId();
                const deviceId = info.identifier;

                await notificationStorage.saveFcmToken({
                    deviceId, fcmToken: token.value
                })

                console.log("đã gửi token này lên Spring Boot:", token.value);
            }
        });

        // 4. Xử lý khi bấm vào thông báo
        PushNotifications.addListener('pushNotificationActionPerformed', async (action) => {
            const data = action.notification.data;
            if (data.conversationId) {
                // router.push(`/chat/${data.conversationId}`);
                const conv = await convStorage.getConversation(data.conversationId)
                if (conv)
                    convStorage.selectConversation(conv)
            }
        });
    };

    return {
        initPush
    }
}