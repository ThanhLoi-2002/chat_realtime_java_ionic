package com.zalo.common.base;

import com.zalo.modules.user.dto.response.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class BaseResponse {
    Long id;
    int stt;

    Long cu;
    UserResponse createdBy;

    LocalDateTime ct;

    Long eu;
    UserResponse updatedBy;

    LocalDateTime et;

    public BaseResponse(BaseEntity e, String... relations) {
        Set<String> rels = relations != null
                ? new HashSet<>(Arrays.asList(relations))
                : Collections.emptySet();

        if (rels.contains("createdBy") && e.getCreatedBy() != null) {
            this.createdBy = new UserResponse(e.getCreatedBy());
        }

        if (rels.contains("updatedBy") && e.getUpdatedBy() != null) {
            this.updatedBy = new UserResponse(e.getUpdatedBy());
        }
    }
}
