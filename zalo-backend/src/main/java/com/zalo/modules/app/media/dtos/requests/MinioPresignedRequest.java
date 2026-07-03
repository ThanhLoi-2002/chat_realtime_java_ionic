package com.zalo.modules.app.media.dtos.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class MinioPresignedRequest {
    String objectName;
}
