package com.zalo.data.remote.fcm;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;
import com.zalo.utils.NotificationHelper;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // LOG CỨNG: Dòng này chắc chắn phải hiện nếu tin nhắn tới máy
        android.util.Log.d("FCM_DEBUG", "========== CÓ TIN NHẮN TỚI ==========");

        // In log để kiểm tra data đổ về
        Log.d("FCM_DEBUG", "Nhận tin nhắn từ Firebase: " + remoteMessage.getData().toString());

        if (!remoteMessage.getData().isEmpty()) {
            Map<String, String> data = remoteMessage.getData();

            String type = data.get("type");
            if ("CHAT_BUBBLE".equals(type)) {
                Log.d("FCM_DEBUG", "CHAT_BUBBLE: " + type);
                NotificationHelper.showChatBubble(
                        this,
                        data.get("conversationId"),
                        data.get("senderName"),
                        data.get("avatar"),
                        data.get("message"));
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // In token ra để bạn copy vào Server Spring Boot test cho chuẩn
        Log.d("FCM_DEBUG", "Token mới của máy: " + token);
    }
}