package com.zalo.modules.classificationCard.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class ClassificationCardRequest {
    String name;
    List<Long> conversationIds;
    String color;
    int position;
}
