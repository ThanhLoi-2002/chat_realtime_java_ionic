package com.zalo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseResponse {
    Long id;
    int stt;

    Long cu;
    LocalDateTime ct;
    Long eu;
    LocalDateTime et;
}
