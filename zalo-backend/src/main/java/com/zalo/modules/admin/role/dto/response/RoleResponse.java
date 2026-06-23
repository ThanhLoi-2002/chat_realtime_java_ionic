package com.zalo.modules.admin.role.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.admin.role.entity.Role;
import com.zalo.modules.admin.structure.entity.AppType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@AllArgsConstructor
public class RoleResponse {
    Long id;
    String name;
    String description;

    AppType appType;
    Long moduleId;
    String module;

    public RoleResponse(Role e) {
        BeanUtils.copyProperties(e, this);
    }
}
