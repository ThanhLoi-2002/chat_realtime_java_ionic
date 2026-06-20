package com.zalo.common.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zalo.modules.app.media.entities.Media;
import com.zalo.modules.app.message.entity.SystemMessageType;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemMetadata {
    private List<Long> addedUsersToGroup;
    private String groupName;
    private Media groupAvatar;
    private SystemMessageType type;
    private Long userId;

    private Long messageId;
    private Long messagePinId;
}
