package com.zalo.modules.classificationCard.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class AssignClassificationCard {
    Long convId;
    String type;
}
