package com.zalo.modules.app.sticker.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.app.sticker.entity.Sticker;
import com.zalo.modules.app.sticker.entity.UserSticker;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserStickerResponse extends BaseResponse {
    String url;
    int frameCount;

    public UserStickerResponse(UserSticker e, String... relations) {
        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");
    }
}
