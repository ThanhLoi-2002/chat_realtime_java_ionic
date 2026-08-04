package com.zalo.modules.admin.oa.category.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.admin.oa.category.entity.Category;
import com.zalo.modules.admin.system.lang.entity.Lang;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse extends BaseResponse {
    String code;
    String name;
    String description;

    public CategoryResponse(Category e, String... relations) {
        super(e, relations);

        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");
    }
}