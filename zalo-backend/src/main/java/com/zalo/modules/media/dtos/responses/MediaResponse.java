package com.zalo.modules.media.dtos.responses;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.media.entities.Media;
import com.zalo.modules.media.entities.MediaType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PUBLIC)
public class MediaResponse extends BaseResponse {
    private MediaType moduleType; // VD: "CONVERSATION", "MESSAGE"
    private Long moduleId;    // ID của message, conversation

    private String publicId;
    private String secureUrl;
    private String resourceType; // "image", "video", "raw"
    private String format;       // "jpg", "mp4", "pdf"
    private Long bytes;          // Dung lượng file
    private Integer width;       // Chỉ cho image/video
    private Integer height;

    public MediaResponse(Media e, String... relations) {
        super(e, relations);
        BeanUtils.copyProperties(e, this,
                "createdBy",
                "updatedBy"
        );
    }
}
