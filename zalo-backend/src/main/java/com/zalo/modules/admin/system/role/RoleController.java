package com.zalo.modules.admin.system.role;

import com.zalo.common.filter.UserFilter;
import com.zalo.modules.admin.system.role.dto.request.AssignPermissionRequest;
import com.zalo.modules.admin.system.role.dto.request.AssignRoleRequest;
import com.zalo.modules.admin.system.role.dto.request.RoleRequest;
import com.zalo.modules.admin.system.role.dto.response.PermissionTreeResponse;
import com.zalo.modules.admin.system.role.dto.response.RoleResponse;
import com.zalo.modules.admin.system.role.dto.response.UserRoleResponse;
import com.zalo.modules.admin.system.role.service.PermissionService;
import com.zalo.modules.admin.system.role.service.RoleService;
import com.zalo.modules.admin.system.role.service.UserRoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/system/role")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;
    PermissionService permissionService;
    UserRoleService userRoleService;

    @PostMapping
    public RoleResponse create(@RequestBody RoleRequest req) {
        return roleService.create(req);
    }

    @PostMapping("set-access")
    public void setAccess(@RequestBody Map<String, List<Long>> req) {
        roleService.setAccess(req);
    }

    @PostMapping("access")
    public Map<String, List<Long>> getAccess(@RequestBody List<String> req) {
        return roleService.getAccess(req);
    }

    @GetMapping
    public List<RoleResponse> getAll() {
        return roleService.getAll();
    }

    @GetMapping("users-roles")
    public Page<UserRoleResponse> getUserRole(@ModelAttribute UserFilter filter) {
        return userRoleService.getUserRole(filter);
    }

    @PutMapping("/{id}")
    public RoleResponse update(@PathVariable Long id, @RequestBody RoleRequest req) {
        return roleService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

    @GetMapping("/permissions")
    public List<PermissionTreeResponse> getPermissions() {
        return permissionService.getAllPermissions();
    }

    @PutMapping("/assign-roles")
    public void assignRoles(
            @RequestBody AssignRoleRequest req
    ) {
        roleService.assignRoles(req);
    }
}
