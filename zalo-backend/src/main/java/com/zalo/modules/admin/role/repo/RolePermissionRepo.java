package com.zalo.modules.admin.role.repo;

import com.zalo.modules.admin.role.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepo
        extends JpaRepository<RolePermission, Long> {

    void deleteByRoleId(Long roleId);

    List<RolePermission> findByRoleId(Long roleId);
}
