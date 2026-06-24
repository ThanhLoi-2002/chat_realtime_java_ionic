package com.zalo.modules.admin.system.user.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.common.entity.File;
import com.zalo.modules.admin.system.user.entities.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserResponse extends BaseResponse {
    String username;
    String phone;
    File avatar;
    File cover;
    int isOa;
    Long oaId;
    Long ownerOaId;

    List<String> roles;
    List<String> permissions;

    public UserResponse(User u, String... relations) {
        super(u, relations);
        BeanUtils.copyProperties(u, this, "createdBy", "updatedBy");
    }
}
