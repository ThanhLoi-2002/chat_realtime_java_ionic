package com.zalo.dto.response.Message;

import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.enums.SystemMessageType;
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
