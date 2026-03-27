package com.zalo.dto.response.Conversation;

import com.zalo.dto.response.BaseResponse;
import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.Conversation;
import com.zalo.model.File;
import com.zalo.model.enums.ConversationType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse extends BaseResponse {
    ConversationType type; // PRIVATE or GROUP

    String name;
    String code;
    File avatar;

    MessageResponse lastMessage;
    UserResponse recipient;

    public ConversationResponse(Conversation c) {
        super(c);
        BeanUtils.copyProperties(c, this, "createdBy", "updatedBy", "lastMessage", "recipient");

        if (c.getLastMessage() != null) {
            this.lastMessage = new MessageResponse(c.getLastMessage());
        }

        if (c.getRecipient() != null) {
            this.recipient = new UserResponse(c.getRecipient());
        }
    }
}
