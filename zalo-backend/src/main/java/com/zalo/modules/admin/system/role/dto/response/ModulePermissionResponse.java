package com.zalo.modules.admin.system.role.dto.response;

import java.util.List;

public record ModulePermissionResponse(
        String module,
        List<String> permissions
) {
}
