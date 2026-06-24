package com.zalo.modules.app.notification.service;

import com.zalo.modules.app.notification.dto.request.SaveFcmTokenRequest;
import com.zalo.modules.admin.system.user.entities.User;
import com.zalo.modules.admin.system.user.service.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    UserRepository userRepository;

    public void saveFcmToken(SaveFcmTokenRequest dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        user.setFcmToken(dto.fcmToken);
        user.setDeviceId(dto.deviceId);
        userRepository.save(user);
    }
}
