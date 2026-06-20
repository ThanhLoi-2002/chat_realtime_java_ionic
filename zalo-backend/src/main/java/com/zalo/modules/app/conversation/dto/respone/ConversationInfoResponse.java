package com.zalo.modules.app.conversation.dto.respone;

import com.zalo.modules.app.conversation.entities.Conversation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class ConversationInfoResponse {
    List<ConversationResponse> generalGroup;

    public ConversationInfoResponse(List<Conversation> generalGroup) {
        this.generalGroup = generalGroup.stream().map(ConversationResponse::new).toList();
    }
}
