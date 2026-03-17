package com.zalo.dto.response.User;

import com.zalo.dto.response.BaseResponse;
import com.zalo.model.File;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse extends BaseResponse {
    String username;
    String phone;
    File avatar;
    File cover;
}
