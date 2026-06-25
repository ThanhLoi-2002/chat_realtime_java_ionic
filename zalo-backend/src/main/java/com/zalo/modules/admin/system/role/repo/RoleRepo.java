package com.zalo.modules.admin.system.role.repo;

import com.zalo.modules.admin.system.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);
    Role findByNameAndIdNot(String name, Long id);
}
