package com.zalo.modules.media.entities;

import com.zalo.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "media")
public class Media extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private MediaType moduleType; // VD: "CONVERSATION", "MESSAGE"
    private Long moduleId;    // ID của message, conversation
    private String name;

    private String publicId;
    private String secureUrl;
    private String resourceType; // "image", "video", "raw"
    private String format;       // "jpg", "mp4", "pdf"
    private Long bytes;          // Dung lượng file
    private Integer width;       // Chỉ cho image/video
    private Integer height;
}
