package com.zalo.modules.oa.officialAccount.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOaRequest {
    @NotBlank(message = "required")
    @Size(max = 150)
    String name;

    String description;
    String category;
    String categoryName;

    String avatar;

    String cover;

    String address;
    int province;
    int district;

//    String startHour;
//    String endHour;
//    boolean isWholeDay;
}
