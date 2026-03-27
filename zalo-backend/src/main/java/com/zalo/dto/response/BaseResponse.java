package com.zalo.dto.response;

import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.BaseEntity;
import com.zalo.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseResponse {
    Long id;
    int stt;

    Long cu;
    UserResponse createdBy;

    LocalDateTime ct;

    Long eu;
    UserResponse updatedBy;

    LocalDateTime et;

    public BaseResponse(BaseEntity e) {
        if(e.getCreatedBy() != null)
        {
            this.createdBy = new UserResponse(e.getCreatedBy());
        }

        if(e.getUpdatedBy() != null)
        {
            this.updatedBy = new UserResponse(e.getUpdatedBy());
        }
    }
}
