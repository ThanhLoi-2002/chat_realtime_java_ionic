package com.zalo.modules.lang.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.lang.entity.Lang;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LangResponse extends BaseResponse {
    String code;
    String en;
    String vi;
    String cn;
    String tw;

    public LangResponse(Lang e, String... relations) {
        super(e, relations);

        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");
    }
}
