package com.zalo.modules.conversation.dto.respone;

import com.zalo.modules.user.dto.response.UserResponse;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.conversation.entities.MemberRole;
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
