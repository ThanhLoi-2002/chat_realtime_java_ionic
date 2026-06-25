package com.zalo.modules.admin.system.role.service;

import com.zalo.common.filter.UserFilter;
import com.zalo.modules.admin.system.role.dto.response.UserRoleResponse;
import com.zalo.modules.admin.system.role.entity.Role;
import com.zalo.modules.admin.system.role.entity.UserRole;
import com.zalo.modules.admin.system.role.repo.RoleRepo;
import com.zalo.modules.admin.system.role.repo.UserRoleRepo;
import com.zalo.modules.admin.system.user.entities.User;
import com.zalo.modules.admin.system.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserRoleService {
    UserRoleRepo userRoleRepo;
    RoleRepo roleRepo;
    UserService userService;

    public Page<UserRoleResponse> getUserRole(UserFilter filter) { // Nên đổi kiểu trả về thành Page thay vì List để giữ phân trang ở Controller
        Page<User> page = userService.findAll(filter);

        return page.map(user -> {
            // Giả sử bạn có UserRoleService hoặc trong User đã có sẵn lấy danh sách role name
            List<Long> roleIds = userRoleRepo.findByUserId(user.getId()).stream().map(UserRole::getRoleId).toList();
            List<String> roles = roleRepo.findAllById(roleIds).stream().map(Role::getName).toList();

            // Trả về constructor bạn đã định nghĩa
            return new UserRoleResponse(user, roles, roleIds);
        });
    }
}
