package com.zalo.modules.oa.officialAccount.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.common.entity.File;
import com.zalo.modules.oa.officialAccount.entity.OaStatus;
import com.zalo.modules.oa.officialAccount.entity.OaVerified;
import com.zalo.modules.oa.officialAccount.entity.OfficialAccount;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OaResponse extends BaseResponse {
    String name;

    String description;

    String category;

    File avatar;

    File cover;

    OaVerified verified;

    OaStatus status;

    public OaResponse (OfficialAccount e, String... relations) {
        super(e, relations);
        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");
    }
}
