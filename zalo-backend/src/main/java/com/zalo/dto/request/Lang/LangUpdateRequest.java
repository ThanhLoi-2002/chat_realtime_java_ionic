package com.zalo.dto.request.Lang;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LangUpdateRequest {
    @Size(min = 1, message = "CODE_INVALID")
    String code;

    String vi;
    String en;
    String tw;
    String cn;
}
