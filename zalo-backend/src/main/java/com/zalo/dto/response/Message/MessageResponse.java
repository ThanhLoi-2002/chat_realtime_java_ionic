package com.zalo.dto.response.Message;

import com.zalo.dto.response.BaseResponse;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.File;
import com.zalo.model.Message;
import com.zalo.model.User;
import com.zalo.model.enums.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse extends BaseResponse {
    Long conversationId;
    String content;
    MessageType contentType;
    File file;

    MessageResponse replyToMessage;

    UserResponse sender;
    public MessageResponse(Message m) {
        super(m);
        BeanUtils.copyProperties(m, this, "replyToMessage", "sender", "createdBy", "updatedBy");


        if(m.getReplyToMessage() != null){
            this.replyToMessage = new MessageResponse(m.getReplyToMessage());
        }

        if (m.getSender() != null) {
            this.sender = new UserResponse(m.getSender());
        }
    }
}
