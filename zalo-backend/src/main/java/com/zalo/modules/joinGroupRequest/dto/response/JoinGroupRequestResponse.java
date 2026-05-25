package com.zalo.modules.joinGroupRequest.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.common.configuration.json.G;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.modules.joinGroupRequest.entity.JoinGroupRequest;
import com.zalo.modules.message.dto.response.MessageResponse;
import com.zalo.modules.user.dto.response.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class JoinGroupRequestResponse extends BaseResponse {
    Long conversationId;
    String message;

    public JoinGroupRequestResponse(JoinGroupRequest e, String... relations) {
        super(e, relations);
        BeanUtils.copyProperties(e, this, "createdBy", "updatedBy");

        this.conversationId = e.getConversationId();
    }
}
