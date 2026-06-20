package com.zalo.modules.app.notification;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.app.notification.dto.request.SaveFcmTokenRequest;
import com.zalo.modules.app.notification.service.NotificationService;
import com.zalo.modules.app.user.dto.response.UserPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @PostMapping("/save-fcm-token")
    public void saveToken(@RequestBody SaveFcmTokenRequest req, @CurrentUser UserPayload user) {
        service.saveFcmToken(req, user.getId());
    }
}
