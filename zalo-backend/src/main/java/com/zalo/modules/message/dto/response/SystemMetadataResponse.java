package com.zalo.modules.message.dto.response;

import com.zalo.common.entity.SystemMetadata;
import com.zalo.modules.media.dtos.responses.MediaResponse;
import com.zalo.modules.user.dto.response.UserResponse;
import com.zalo.modules.message.entity.SystemMessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class SystemMetadataResponse {
    SystemMessageType type;
    List<UserResponse> addedUsersToGroup;
    String groupName;
    UserResponse user;
    MediaResponse groupAvatar;

    public SystemMetadataResponse(SystemMetadata s) {
        BeanUtils.copyProperties(s, this, "groupAvatar");

//        if(s.getAddedUsersToGroup() != null){
//            this.addedUsersToGroup = s.getAddedUsersToGroup().stream().map(e -> new UserResponse(e)).toList();
//        }

        if(s.getGroupAvatar() != null) {
            this.groupAvatar = new MediaResponse(s.getGroupAvatar());
        }
    }
}
