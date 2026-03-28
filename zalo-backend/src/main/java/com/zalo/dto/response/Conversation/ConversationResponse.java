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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    public ConversationResponse(Conversation c, String... relations) {
        super(c, relations);
        BeanUtils.copyProperties(c, this, "createdBy", "updatedBy", "lastMessage", "recipient");

        Set<String> rels = relations != null
                ? new HashSet<>(Arrays.asList(relations))
                : Collections.emptySet();

        if (rels.contains("lastMessage")) {
            this.lastMessage = new MessageResponse(c.getLastMessage(), "sender");
        }

        if (rels.contains("recipient")) {
            this.recipient = new UserResponse(c.getRecipient());
        }
    }
}
