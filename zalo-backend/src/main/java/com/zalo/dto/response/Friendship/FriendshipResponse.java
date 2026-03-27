package com.zalo.dto.response.Friendship;

import com.zalo.dto.response.BaseResponse;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.Friendship;
import com.zalo.model.User;
import com.zalo.model.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendshipResponse extends BaseResponse {
    UserResponse user1;

    UserResponse user2;

    FriendStatus status;

    Long actionUserId; // ai là người gửi request

    String content;

    public FriendshipResponse(Friendship e) {
        super(e);

        BeanUtils.copyProperties(e, this,
                "user1",
                "user2",
                "createdBy",
                "updatedBy"
        );

        if (e.getUser1() != null) {
            this.user1 = new UserResponse(e.getUser1());
        }

        if (e.getUser2() != null) {
            this.user2 = new UserResponse(e.getUser2());
        }
    }
}
