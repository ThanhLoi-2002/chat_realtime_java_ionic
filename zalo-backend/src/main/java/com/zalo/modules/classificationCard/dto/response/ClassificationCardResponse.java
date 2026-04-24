package com.zalo.modules.classificationCard.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.classificationCard.entity.ClassificationCard;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassificationCardResponse extends BaseResponse {
    String name;
    String color;
    int position;

    public ClassificationCardResponse(ClassificationCard e, String... relations) {
        super(e, relations);

        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");
    }
}
