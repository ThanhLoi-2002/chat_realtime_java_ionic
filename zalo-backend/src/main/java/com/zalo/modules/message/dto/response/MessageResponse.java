package com.zalo.modules.message.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.user.dto.response.UserResponse;
import com.zalo.common.entity.File;
import com.zalo.modules.message.entity.Message;
import com.zalo.modules.message.entity.MessageType;
import com.zalo.modules.media.dtos.responses.MediaResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.*;

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

    List<MessageReactionResponse> reactions = List.of();

    SystemMetadataResponse systemMetadata;

    List<MediaResponse> attachments;

    public MessageResponse(Message m, String... relations) {
        super(m, relations);
        BeanUtils.copyProperties(m, this, "replyToMessage", "sender", "createdBy", "updatedBy");

        Set<String> rels = relations != null ? new HashSet<>(Arrays.asList(relations)) : Collections.emptySet();

        if (rels.contains("replyToMessage")) {
            if (m.getReplyToMessage() != null) {
                this.replyToMessage = new MessageResponse(m.getReplyToMessage());
            }
        }

        if (rels.contains("sender")) {
            if (m.getSender() != null) {
                this.sender = new UserResponse(m.getSender());
            }
        }

//        this.attachments = m.getAttachments().stream().map(MediaResponse::new).toList();
    }
}
