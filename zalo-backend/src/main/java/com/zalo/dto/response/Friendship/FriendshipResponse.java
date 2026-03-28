package com.zalo.dto.response.Friendship;

import com.zalo.dto.response.BaseResponse;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.Friendship;
import com.zalo.model.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    public FriendshipResponse(Friendship e, String... relations) {
        super(e, relations);

        BeanUtils.copyProperties(e, this,
                "user1",
                "user2",
                "createdBy",
                "updatedBy"
        );

        Set<String> rels = relations != null
                ? new HashSet<>(Arrays.asList(relations))
                : Collections.emptySet();

        if (rels.contains("user1")) {
            this.user1 = new UserResponse(e.getUser1());
        }

        if (rels.contains("user2")) {
            this.user2 = new UserResponse(e.getUser2());
        }
    }
}
