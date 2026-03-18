package com.zalo.dto.response.Lang;

import com.zalo.dto.response.BaseResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

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
}
