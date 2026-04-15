package com.zalo.modules.message.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class LinkPreviewResponse {
    String title;
    String description;
    String image;
    String url;
}
