package com.zalo.modules.admin.role;

import com.zalo.modules.admin.role.dto.request.AssignPermissionRequest;
import com.zalo.modules.admin.role.dto.request.AssignRoleRequest;
import com.zalo.modules.admin.role.dto.request.RoleRequest;
import com.zalo.modules.admin.role.dto.response.PermissionResponse;
import com.zalo.modules.admin.role.entity.Role;
import com.zalo.modules.admin.role.service.PermissionService;
import com.zalo.modules.admin.role.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;
    PermissionService permissionService;

    @PostMapping
    public Role create(@RequestBody RoleRequest req) {
        return roleService.create(req);
    }

    @GetMapping
    public List<Role> getAll() {
        return roleService.getAll();
    }

    @PutMapping("/{id}")
    public Role update(@PathVariable Long id, @RequestBody RoleRequest req) {
        return roleService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

    @GetMapping("/permissions")
    public List<PermissionResponse> getPermissions() {
        return permissionService.getAllPermissions();
    }

    @PutMapping("/{id}/permissions")
    public void assignPermissions(
            @PathVariable Long id,
            @RequestBody AssignPermissionRequest req
    ) {
        roleService.assignPermissions(id, req);
    }

    @PutMapping("/user/{userid}/roles")
    public void assignRoles(
            @PathVariable Long userid,
            @RequestBody AssignRoleRequest req
    ) {
        roleService.assignRoles(userid, req);
    }
}
