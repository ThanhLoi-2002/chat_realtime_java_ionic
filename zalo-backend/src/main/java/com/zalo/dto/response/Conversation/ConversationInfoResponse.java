package com.zalo.dto.response.Conversation;

import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.model.Conversation;
import com.zalo.model.File;
import com.zalo.model.Message;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class ConversationInfoResponse {
    List<ConversationResponse> generalGroup;
    List<MessageResponse> messages;

    public ConversationInfoResponse(List<Conversation> generalGroup, List<Message> images) {
        this.generalGroup = generalGroup.stream().map(ConversationResponse::new).toList();
        this.messages = images.stream().map(m -> new MessageResponse(m, "sender")).toList();
    }
}
