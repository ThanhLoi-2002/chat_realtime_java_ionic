package com.zalo.ui.main;

import com.getcapacitor.BridgeActivity;
import com.zalo.utils.NotificationHelper;

import android.os.Bundle; // Fixes the 'Bundle' error

public class MainActivity extends BridgeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. Khởi tạo Kênh thông báo hỗ trợ Bubble
        NotificationHelper.createNotificationChannel(this);
    }
}
