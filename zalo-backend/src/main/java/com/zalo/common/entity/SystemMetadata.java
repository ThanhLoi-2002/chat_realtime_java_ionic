package com.zalo.common.entity;

import com.zalo.modules.message.entity.SystemMessageType;
import lombok.Data;

import java.util.List;

@Data
public class SystemMetadata {
    private List<Long> addedUsersToGroup;
    private SystemMessageType type;
}
