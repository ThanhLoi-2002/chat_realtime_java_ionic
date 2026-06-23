package com.zalo.modules.admin.role.repo;

import com.zalo.modules.admin.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);
    Role findByNameAndIdNot(String name, Long id);
}
