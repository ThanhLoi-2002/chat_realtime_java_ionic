package com.zalo.modules.sticker.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.message.dto.response.MessageResponse;
import com.zalo.modules.message.dto.response.SystemMetadataResponse;
import com.zalo.modules.message.entity.Message;
import com.zalo.modules.sticker.entity.Sticker;
import com.zalo.modules.sticker.entity.StickerItem;
import com.zalo.modules.user.dto.response.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StickerResponse {
    String name;
    String thumbImg;
    String iconUrl;
    String source;
    List<StickerItem> items;

    public StickerResponse(Sticker e, String... relations) {
        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");
    }
}
