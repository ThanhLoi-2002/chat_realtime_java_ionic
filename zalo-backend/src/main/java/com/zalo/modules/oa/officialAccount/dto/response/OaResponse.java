package com.zalo.modules.oa.officialAccount.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.oa.officialAccount.entity.OaDisplay;
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
    String code;

    String description;

    String category;
    String categoryName;

    String avatar;

    String cover;

    int province;
    int district;

    String startHour;
    String endHour;
    boolean isWholeDay;

    OaVerified verified;

    OaStatus status;
    OaDisplay display;

    public OaResponse (OfficialAccount e, String... relations) {
        super(e, relations);
        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");
    }
}
