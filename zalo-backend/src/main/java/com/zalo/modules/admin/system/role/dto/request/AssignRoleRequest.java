package com.zalo.modules.admin.system.role.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class AssignRoleRequest {
    Long id;
    List<Long> roleIds;
}
