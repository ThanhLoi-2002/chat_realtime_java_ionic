package com.zalo.modules.admin.role.dto.response;

import java.util.List;

public record ModulePermissionResponse(
        String module,
        List<String> permissions
) {
}
