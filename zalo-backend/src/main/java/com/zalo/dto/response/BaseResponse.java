package com.zalo.dto.response;

import com.zalo.dto.response.User.UserResponse;
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

    public BaseResponse(User createdBy, User updatedBy) {
        if(createdBy != null)
        {
            this.createdBy = new UserResponse(createdBy);
        }

        if(updatedBy != null)
        {
            this.updatedBy = new UserResponse(createdBy);
        }
    }
}
