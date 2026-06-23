package com.zalo.modules.admin.role.dto.request;

import com.zalo.modules.admin.structure.entity.AppType;
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
