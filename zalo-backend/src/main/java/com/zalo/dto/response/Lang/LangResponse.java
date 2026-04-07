package com.zalo.dto.response.Lang;

import com.zalo.dto.response.BaseResponse;
import com.zalo.model.Lang;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
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
