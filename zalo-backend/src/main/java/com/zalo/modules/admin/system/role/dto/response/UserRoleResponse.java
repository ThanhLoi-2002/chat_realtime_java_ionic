package com.zalo.modules.admin.system.role.dto.response;

import com.zalo.modules.admin.system.user.dto.response.UserResponse;
import com.zalo.modules.admin.system.user.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserRoleResponse {
    UserResponse user;
    List<String> roles;
    List<Long> roleIds;

    public UserRoleResponse(User u, List<String> roles, List<Long> roleIds) {
        this.user = new UserResponse(u);
        this.roles = roles;
        this.roleIds = roleIds;
    }
}
