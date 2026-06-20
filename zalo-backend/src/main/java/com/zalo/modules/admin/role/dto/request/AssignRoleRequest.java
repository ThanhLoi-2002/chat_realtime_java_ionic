package com.zalo.modules.admin.role.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class AssignRoleRequest {

    List<Long> roleIds;
}
