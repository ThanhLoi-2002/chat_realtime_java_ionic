package com.zalo.common.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {
    public void sendAdvancedNotification(String token, String title, String body, Long conversationId, String imageUrl, String channelId) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .putData("conversationId", conversationId.toString()) // Gửi ID cuộc trò chuyện
                    .putData("type", "MESSAGE")
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(imageUrl)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH) // Ưu tiên cao nhất
                            .setNotification(AndroidNotification.builder()
                                    .setChannelId(channelId) // Khớp với ID ở Bước 2
                                    .setSound("default")
                                    .setDefaultSound(true)
                                    .setDefaultVibrateTimings(true)
                                    .build())
                            .build())
                    .build();

            FirebaseMessaging.getInstance().send(message);
            System.out.println("đã gửi thoong báo");
        } catch (FirebaseMessagingException e) {
//            throw new RuntimeException(e);
        }
    }

    public void sendBubbleNotification(String token, String senderName, String messageText,  Long conversationId, String imageUrl) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    // CHỈ DÙNG putData, KHÔNG DÙNG setNotification
                    .putData("type", "CHAT_BUBBLE")
                    .putData("conversationId", conversationId.toString())
                    .putData("senderName", senderName)
                    .putData("avatar", imageUrl)
                    .putData("message", messageText)
                    .build();

            FirebaseMessaging.getInstance().send(message);
            System.out.println("đã gửi CHAT_BUBBLE");
        } catch (FirebaseMessagingException e) {
//            throw new RuntimeException(e);
        }
    }
}
