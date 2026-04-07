package com.zalo.model.metadata;

import com.zalo.model.enums.SystemMessageType;
import lombok.Data;

import java.util.List;

@Data
public class SystemMetadata {
    private List<Long> addedUsersToGroup;
    private SystemMessageType type;
}
