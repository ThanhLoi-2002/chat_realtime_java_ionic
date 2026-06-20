package com.zalo.modules.user.dto.response;

import com.zalo.common.entity.File;
import com.zalo.modules.user.entities.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserPayload {
    Long id;
    String username;
    String phone;
//    File avatar;
//    File cover;

    int isOa;
    Long oaId;
    Long ownerOaId;

    List<String> roles;
    List<String> permissions;
    int stt;

    public UserPayload(User u) {
        BeanUtils.copyProperties(u, this);
    }
}
