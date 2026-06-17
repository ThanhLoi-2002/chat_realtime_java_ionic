package com.zalo.modules.admin.role.service;

import com.zalo.modules.admin.role.dto.request.RoleRequest;
import com.zalo.modules.admin.role.entity.Role;
import com.zalo.modules.admin.role.repo.RoleRepo;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoleService {
    RoleRepo roleRepo;

    public Role create(RoleRequest req) {
        Role existed = roleRepo.findByName(req.getName());
        if (existed != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "nameExisted");
        }

        Role role = new Role();
        role.setName(req.getName());
        role.setDescription(req.getDescription());
        return roleRepo.save(role);
    }

    public List<Role> getAll() {
        return roleRepo.findAll();
    }

    public Role getById(Long id) {
        return roleRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "roleNotFound"));
    }

    public Role update(Long id, RoleRequest req) {
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "roleNotFound"));

        // Nếu đổi tên thì kiểm tra tên mới đã bị trùng với role khác chưa
        if (!role.getName().equals(req.getName())) {
            Role existed = roleRepo.findByName(req.getName());
            if (existed != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "nameExisted");
            }
        }

        role.setName(req.getName());
        role.setDescription(req.getDescription());
        return roleRepo.save(role);
    }

    public void delete(Long id) {
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "roleNotFound"));
        roleRepo.delete(role);
    }
}
