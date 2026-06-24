package com.zalo.modules.admin.system.role.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignPermissionRequest {

    private List<String> permissions;
}
