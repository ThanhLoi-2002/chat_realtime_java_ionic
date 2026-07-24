package com.zalo.modules.oa.officialAccount.dto.request;

import com.zalo.common.entity.File;
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

    @NotBlank(message = "required")
    String category;

    File avatar;

    File cover;
}
