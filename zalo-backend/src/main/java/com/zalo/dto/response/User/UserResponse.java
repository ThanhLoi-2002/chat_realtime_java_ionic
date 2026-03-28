package com.zalo.dto.response.User;

import com.zalo.dto.response.BaseResponse;
import com.zalo.model.File;
import com.zalo.model.User;
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

    public UserResponse(User u, String... relations){
        super(u, relations);
        BeanUtils.copyProperties(u, this, "createdBy", "updatedBy");
    }
}
