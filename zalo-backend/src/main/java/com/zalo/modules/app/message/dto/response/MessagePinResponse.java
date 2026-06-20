package com.zalo.modules.app.message.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.app.message.entity.MessagePin;
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
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class MessagePinResponse extends BaseResponse {
    Long conversationId;
    Long messageId;
    int isActive = 1;

    MessageResponse message;

    public MessagePinResponse(MessagePin e, String... relations) {
        super(e, relations);
        BeanUtils.copyProperties(e, this, "message", "createdBy", "updatedBy");

        Set<String> rels = relations != null ? new HashSet<>(Arrays.asList(relations)) : Collections.emptySet();

        if (rels.contains("message")) {
            this.message = new MessageResponse(e.getMessage(), "sender");
        }
    }
}
