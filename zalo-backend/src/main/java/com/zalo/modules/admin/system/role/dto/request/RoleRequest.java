package com.zalo.modules.admin.system.role.dto.request;

import com.zalo.modules.admin.system.structure.entity.AppType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
    String name;
    String description;
    AppType appType;
    Long moduleId;
}
