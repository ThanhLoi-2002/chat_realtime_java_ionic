package com.zalo.modules.admin.system.role.repo;

import com.zalo.modules.admin.system.role.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepo
        extends JpaRepository<RolePermission, Long> {

    void deleteByPermission(String permission);

    List<RolePermission> findByPermissionIn(List<String> permissions);
}
