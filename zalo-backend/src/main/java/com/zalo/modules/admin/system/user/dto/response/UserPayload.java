package com.zalo.modules.admin.system.user.dto.response;

import com.zalo.modules.admin.system.user.entities.User;
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
