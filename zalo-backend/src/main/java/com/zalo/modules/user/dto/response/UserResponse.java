package com.zalo.modules.user.dto.response;

import com.zalo.dto.response.BaseResponse;
import com.zalo.model.File;
import com.zalo.modules.user.entities.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

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

    public UserResponse(User u, String... relations) {
        super(u, relations);
        BeanUtils.copyProperties(u, this, "createdBy", "updatedBy");
    }
}
