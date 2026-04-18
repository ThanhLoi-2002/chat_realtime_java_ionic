package com.zalo.modules.notification.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class SaveFcmTokenRequest {
    String deviceId;
    String fcmToken;
}
