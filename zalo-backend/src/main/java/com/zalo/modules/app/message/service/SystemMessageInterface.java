package com.zalo.modules.app.message.service;

import com.zalo.modules.app.message.dto.request.CreateSystemMessageRequest;
import com.zalo.modules.app.message.dto.response.MessageResponse;
import com.zalo.modules.app.message.entity.Message;

public interface SystemMessageInterface {
    void createSystemMessage(CreateSystemMessageRequest dto);
    void getSystemMetadata(Message e, MessageResponse rp);
}
