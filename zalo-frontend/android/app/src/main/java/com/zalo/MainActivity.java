package com.zalo;

import com.getcapacitor.BridgeActivity;
import android.os.Bundle; // Fixes the 'Bundle' error
import android.graphics.Bitmap; // Fixes the 'Bitmap' error

public class MainActivity extends BridgeActivity {
    private static final String CHANNEL_ID = "chatBubblesChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. Khởi tạo Kênh thông báo hỗ trợ Bubble
        NotificationHelper.createNotificationChannel(this);
    }
}
