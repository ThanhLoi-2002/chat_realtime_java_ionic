package com.zalo.modules.conversation.dto.respone;

import com.zalo.common.base.BaseResponse;
import com.zalo.common.configuration.json.G;
import com.zalo.modules.media.dtos.responses.MediaResponse;
import com.zalo.modules.message.dto.response.MessageResponse;
import com.zalo.modules.user.dto.response.UserResponse;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.common.entity.File;
import com.zalo.modules.conversation.entities.ConversationType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse extends BaseResponse {
    ConversationType type; // PRIVATE or GROUP

    String name;
    String inviteCode;
    String description;
    MediaResponse avatar;
    Long lastMessageId;

    Long ownerId;
    UserResponse owner;

    MessageResponse lastMessage;
    UserResponse recipient;

    List<MemberResponse> members;
    Long unread;
    Boolean isMention;

    public ConversationResponse(Conversation c, String... relations) {
        super(c, relations);
        BeanUtils.copyProperties(c, this, "createdBy", "updatedBy", "lastMessage", "recipient", "owner", "avatar");

        Set<String> rels = relations != null
                ? new HashSet<>(Arrays.asList(relations))
                : Collections.emptySet();

        if (rels.contains("lastMessage")) {
            if(c.getLastMessage() != null) {
                this.lastMessage = new MessageResponse(c.getLastMessage(), "sender");
            }
        }

        if (rels.contains("recipient")) {
            if(c.getRecipient() != null){
                this.recipient = new UserResponse(c.getRecipient());
            }
        }

        if (rels.contains("owner")) {
            if(c.getOwner() != null){
                this.owner = new UserResponse(c.getOwner());
            }
        }

        if (rels.contains("avatar")) {
            if(c.getAvatar() != null){
                this.avatar = new MediaResponse(c.getAvatar());
            }
        }
    }
}
