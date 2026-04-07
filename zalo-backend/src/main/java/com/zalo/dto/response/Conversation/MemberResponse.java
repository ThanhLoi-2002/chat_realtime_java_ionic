package com.zalo.dto.response.Conversation;

import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.User;
import com.zalo.model.enums.MemberRole;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberResponse extends UserResponse {
    MemberRole role;
    Long addById;
    UserResponse addBy;

    public MemberResponse(User u, MemberRole role, User inviter, String... relations){
        super(u, relations);
        this.role = role;

        if(inviter != null){
            this.addById = inviter.getId();
            this.addBy = new UserResponse(inviter);
        }
    }
}
