package com.zalo.modules.message.dto.response;

import com.zalo.modules.user.dto.response.UserResponse;
import com.zalo.modules.message.entity.SystemMessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class SystemMetadataResponse {
    SystemMessageType type;
    List<UserResponse> addedUsersToGroup;
}
