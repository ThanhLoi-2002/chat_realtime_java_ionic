package com.zalo.modules.admin.system.role.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PermissionResponse {
    String group;
    List<String> permissions;
}
