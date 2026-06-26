package com.zalo.modules.admin.system.structure.dto.response;

import com.zalo.modules.admin.system.structure.entity.AppType;
import com.zalo.modules.admin.system.structure.entity.MenuType;
import com.zalo.modules.admin.system.structure.entity.Structure;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class StructureResponse {
    Long id;
    AppType appType;
    Long pid;
    String code;
    String icon;
    String description;
    Integer type;
    Integer sort;
    String permissions;
    String component;
    String path;
    MenuType menuType;
    List<StructureResponse> children = new ArrayList<>();

    public StructureResponse(Structure e) {
        BeanUtils.copyProperties(e, this);
    }
}
