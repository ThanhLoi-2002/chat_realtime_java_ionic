package com.zalo.modules.admin.role.service;

import com.zalo.modules.admin.role.dto.request.AssignPermissionRequest;
import com.zalo.modules.admin.role.dto.request.AssignRoleRequest;
import com.zalo.modules.admin.role.dto.request.RoleRequest;
import com.zalo.modules.admin.role.dto.response.RoleResponse;
import com.zalo.modules.admin.role.entity.Role;
import com.zalo.modules.admin.role.entity.RolePermission;
import com.zalo.modules.admin.role.entity.UserRole;
import com.zalo.modules.admin.role.repo.RolePermissionRepo;
import com.zalo.modules.admin.role.repo.RoleRepo;
import com.zalo.modules.admin.role.repo.UserRoleRepo;
import com.zalo.modules.admin.structure.entity.Structure;
import com.zalo.modules.admin.structure.service.StructureRepository;
import com.zalo.modules.app.user.service.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    public void assignPermissions(
            Long roleId,
            AssignPermissionRequest req
    ) {

        roleRepo.findById(roleId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "roleNotFound"));

        rolePermissionRepo.deleteByRoleId(roleId);

        List<RolePermission> entities =
                req.getPermissions()
                        .stream()
                        .distinct()
                        .map(permission -> {

                            RolePermission rp =
                                    new RolePermission();

                            rp.setRoleId(roleId);
                            rp.setPermission(permission);

                            return rp;
                        })
                        .toList();

        rolePermissionRepo.saveAll(entities);
    }

    public void assignRoles(
            Long userId,
            AssignRoleRequest req
    ) {

        userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "userNotFound"));

        userRoleRepo.deleteByUserId(userId);

        List<UserRole> entities =
                req.getRoleIds()
                        .stream()
                        .distinct()
                        .map(roleId -> {

                            UserRole ur = new UserRole();

                            ur.setUserId(userId);
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
