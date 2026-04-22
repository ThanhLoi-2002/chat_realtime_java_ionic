package com.zalo.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.content.LocusId;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.zalo.ui.main.MainActivity;
import com.zalo.R;

public class NotificationHelper {
    public static final String CHANNEL_ID = "chat_bubbles_channel";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Tin nhắn Bong bóng", NotificationManager.IMPORTANCE_HIGH);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                channel.setAllowBubbles(true);
            }
            
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d("FCM_DEBUG", "Đã tạo Notification Channel cho Bubble");
            }
        }
    }

    public static void showChatBubble(Context context, String conversationId, String senderName, String avatarUrl, String messageText) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return;

        new Thread(() -> {
            try {
                // 1. Tải ảnh Avatar (Xử lý lỗi nếu URL die)
                Icon icon;
                try {
                    URL url = new URL(avatarUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream input = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    icon = Icon.createWithAdaptiveBitmap(bitmap);
                } catch (Exception e) {
                    // Nếu lỗi ảnh, dùng icon mặc định của App
                    icon = Icon.createWithResource(context, R.mipmap.ic_launcher);
                }

                // 2. Tạo Shortcut (Bắt buộc phải có để hiện Bubble)
                Person person = new Person.Builder()
                        .setName(senderName)
                        .setIcon(icon)
                        .build();

                ShortcutInfo shortcut = new ShortcutInfo.Builder(context, conversationId)
                        .setShortLabel(senderName)
                        .setIcon(icon)
                        .setLongLived(true)
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("com.zalo://chat/" + conversationId)))
                        .setPerson(person)
                        .setLongLived(true) // Quan trọng cho Bubble
                        .build();

                ShortcutManager sm = context.getSystemService(ShortcutManager.class);
                if (sm != null) sm.pushDynamicShortcut(shortcut);

                // 3. Tạo Bubble Metadata
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 
                        PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                
                Notification.BubbleMetadata bubble = new Notification.BubbleMetadata.Builder(pi, icon)
                        .setDesiredHeight(600)
                        .setAutoExpandBubble(true)
                        .build();

                // 4. Build Notification
                Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                        .setContentTitle(senderName)
                        .setContentText(messageText)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setBubbleMetadata(bubble)
                        .setShortcutId(conversationId)
                        .addPerson(person)
                        .setShowWhen(true)
                        .setLocusId(new LocusId(conversationId))
                        .setCategory(Notification.CATEGORY_MESSAGE); // Phân loại là tin nhắn

                NotificationManager nm = context.getSystemService(NotificationManager.class);
                if (nm != null) {
                    nm.notify(conversationId.hashCode(), builder.build());
                    Log.d("FCM_DEBUG", "Đã bắn thông báo Bubble cho: " + senderName);
                }

            } catch (Exception e) {
                Log.e("FCM_ERROR", "Lỗi hiển thị Bubble: " + e.getMessage());
            }
        }).start();
    }
}