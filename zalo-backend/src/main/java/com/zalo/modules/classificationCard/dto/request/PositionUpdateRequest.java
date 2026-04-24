package com.zalo.modules.classificationCard.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class PositionUpdateRequest {
    Long id;
    Integer position;
}
