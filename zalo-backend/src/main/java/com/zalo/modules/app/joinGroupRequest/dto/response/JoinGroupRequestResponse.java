package com.zalo.modules.app.joinGroupRequest.dto.response;

import com.zalo.common.base.BaseResponse;
import com.zalo.modules.app.joinGroupRequest.entity.JoinGroupRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;

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
