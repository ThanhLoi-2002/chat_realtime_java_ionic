package com.zalo.modules.app.sticker.dto.response;

import com.zalo.modules.app.sticker.entity.Sticker;
import com.zalo.modules.app.sticker.entity.StickerItem;
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
    String stickerId;
//    String thumbImg;
    String iconUrl;
    String source;
    List<StickerItem> items;

    public StickerResponse(Sticker e, String... relations) {
        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");
    }
}
