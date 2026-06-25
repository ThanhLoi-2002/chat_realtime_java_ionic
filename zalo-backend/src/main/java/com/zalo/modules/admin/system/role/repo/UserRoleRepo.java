package com.zalo.modules.admin.system.role.repo;

import com.zalo.modules.admin.system.role.entity.Role;
import com.zalo.modules.admin.system.role.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepo
        extends JpaRepository<UserRole, Long> {

    void deleteByUserId(Long userId);

    List<UserRole> findByUserId(Long userId);

    @Query("""
        SELECT DISTINCT rp.permission
        FROM UserRole ur,
             RolePermission rp
        WHERE ur.roleId = rp.roleId
        AND ur.userId = :userId
    """)
    List<String> getPermissions(Long userId);

    @Query("""
        SELECT r.name
        FROM UserRole ur
        JOIN Role r ON ur.roleId = r.id
        WHERE ur.userId = :userId
    """)
    List<String> getRoles(Long userId);
}
