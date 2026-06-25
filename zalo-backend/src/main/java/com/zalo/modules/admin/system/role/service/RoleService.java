package com.zalo.modules.admin.system.role.service;

import com.zalo.modules.admin.system.role.dto.request.AssignPermissionRequest;
import com.zalo.modules.admin.system.role.dto.request.AssignRoleRequest;
import com.zalo.modules.admin.system.role.dto.request.RoleRequest;
import com.zalo.modules.admin.system.role.dto.response.RoleResponse;
import com.zalo.modules.admin.system.role.entity.Role;
import com.zalo.modules.admin.system.role.entity.RolePermission;
import com.zalo.modules.admin.system.role.entity.UserRole;
import com.zalo.modules.admin.system.role.repo.RolePermissionRepo;
import com.zalo.modules.admin.system.role.repo.RoleRepo;
import com.zalo.modules.admin.system.role.repo.UserRoleRepo;
import com.zalo.modules.admin.system.structure.entity.Structure;
import com.zalo.modules.admin.system.structure.service.StructureRepository;
import com.zalo.modules.admin.system.user.service.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoleService {
    RoleRepo roleRepo;
    RolePermissionRepo rolePermissionRepo;
    UserRepository userRepo;
    UserRoleRepo userRoleRepo;
    StructureRepository structureRepository;

    public RoleResponse create(RoleRequest req) {
        Role existed = roleRepo.findByName(req.getName());
        if (existed != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "nameExisted");
        }

        Role role = new Role();
        role.setName(req.getName());
        role.setDescription(req.getDescription());
        role.setAppType(req.getAppType());
        role.setModuleId(req.getModuleId());
        roleRepo.save(role);

        return getRoleModule(role);
    }

    private RoleResponse getRoleModule(Role role) {
        Optional<Structure> structure = structureRepository.findById(role.getId());

        RoleResponse res = new RoleResponse(role);
        structure.ifPresent(value -> res.setModule(value.getCode()));

        return res;
    }

    public List<RoleResponse> getAll() {
        List<Role> roles = roleRepo.findAll();

        // Lấy danh sách moduleId
        List<Long> moduleIds = roles.stream()
                .map(Role::getModuleId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // Query 1 lần
        Map<Long, String> moduleMap = structureRepository.findAllById(moduleIds)
                .stream()
                .collect(Collectors.toMap(Structure::getId, Structure::getCode));

        // Map response
        return roles.stream()
                .map(role -> {
                    RoleResponse res = new RoleResponse(role);

                    String module = moduleMap.get(role.getModuleId());
                    if (module != null) {
                        res.setModule(module);
                    }

                    return res;
                })
                .toList();
    }

    public RoleResponse update(Long id, RoleRequest req) {
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "roleNotFound"));

        Role existed = roleRepo.findByNameAndIdNot(
                req.getName(),
                id
        );

        if (existed != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "nameExisted"
            );
        }

        role.setName(req.getName());
        role.setDescription(req.getDescription());
        role.setAppType(req.getAppType());
        role.setModuleId(req.getModuleId());
        roleRepo.save(role);

        return getRoleModule(role);
    }

    public void delete(Long id) {
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "roleNotFound"));
        roleRepo.delete(role);
    }

    public void setAccess(
            Map<String, List<Long>> req
    ) {
        for (Map.Entry<String, List<Long>> entry : req.entrySet()) {
            String permission = entry.getKey();
            List<Long> roleIds = entry.getValue();

            rolePermissionRepo.deleteByPermission(permission);
            List<RolePermission> entities =
                    roleIds
                            .stream()
                            .distinct()
                            .map(id -> {

                                RolePermission rp =
                                        new RolePermission();

                                rp.setRoleId(id);
                                rp.setPermission(permission);

                                return rp;
                            })
                            .toList();
            rolePermissionRepo.saveAll(entities);
        }
    }

    public Map<String, List<Long>> getAccess(
            List<String> permissions
    ) {
        List<RolePermission> list = rolePermissionRepo.findByPermissionIn(permissions);
        Map<String, List<Long>> result = new HashMap<>();
        for (RolePermission item : list) {
            String permission = item.getPermission();
            Long roleId = item.getRoleId();

            result.computeIfAbsent(permission, k -> new ArrayList<>())
                    .add(roleId);
        }

        return result;
    }

    public void assignRoles(
            AssignRoleRequest req
    ) {

        userRepo.findById(req.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "userNotFound"));

        userRoleRepo.deleteByUserId(req.getId());

        List<UserRole> entities =
                req.getRoleIds()
                        .stream()
                        .distinct()
                        .map(roleId -> {

                            UserRole ur = new UserRole();

                            ur.setUserId(req.getId());
                            ur.setRoleId(roleId);

                            return ur;
                        })
                        .toList();

        userRoleRepo.saveAll(entities);
    }

    public List<String> getUserRoles(Long userId) {
        return userRoleRepo.getRoles(userId);
    }

    public List<String> getUserPermissions(Long userId) {
        return userRoleRepo.getPermissions(userId);
    }
}
