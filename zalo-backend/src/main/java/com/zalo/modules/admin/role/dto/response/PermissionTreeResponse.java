package com.zalo.modules.admin.role.dto.response;

import java.util.List;

public record PermissionTreeResponse(
        String app,
        List<ModulePermissionResponse> modules
) {
}
