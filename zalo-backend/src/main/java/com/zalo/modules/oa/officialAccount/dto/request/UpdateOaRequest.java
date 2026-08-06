package com.zalo.modules.oa.officialAccount.dto.request;

import com.zalo.modules.oa.officialAccount.entity.OaDisplay;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOaRequest {
    @NotBlank(message = "required")
    @Size(max = 150)
    String name;

    String description;
    String phone;

//    @NotBlank(message = "required")
    String category;

    String avatar;

    String cover;

    int province;
    int district;
    String address;

    String startHour;
    String endHour;
    Boolean isWholeDay;
    OaDisplay display;
}
