package com.zalo.modules.admin.system.role.dto.response;

import java.util.List;

public record PermissionTreeResponse(
        String app,
        List<ModulePermissionResponse> modules
) {
}
