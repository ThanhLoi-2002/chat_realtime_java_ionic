package com.zalo.modules.oa.officialAccount.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OaDisplay {
    boolean showDescription;
    boolean showAddress;
    boolean showPhone;
    boolean showWebsite;
    boolean showWorkingHours;
    boolean showCallButton;
}
