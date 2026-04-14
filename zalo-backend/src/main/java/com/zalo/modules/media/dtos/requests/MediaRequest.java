package com.zalo.modules.media.dtos.requests;

import com.zalo.modules.media.entities.MediaType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class MediaRequest {
    MediaType moduleType; // VD: "CONVERSATION", "MESSAGE"
    Long moduleId;
    String name;

    String publicId;
    String secureUrl;
    String resourceType; // "image", "video", "raw"
    String format;       // "jpg", "mp4", "pdf"
    Long bytes;          // Dung lượng file
    Integer width;       // Chỉ cho image/video
    Integer height;
}
