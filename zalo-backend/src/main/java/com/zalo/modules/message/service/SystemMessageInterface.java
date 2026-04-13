package com.zalo.modules.message.service;

import com.zalo.modules.message.dto.request.CreateSystemMessageRequest;
import com.zalo.modules.message.dto.response.MessageResponse;
import com.zalo.modules.message.entity.Message;

public interface SystemMessageInterface {
    void createSystemMessage(CreateSystemMessageRequest dto);
    void getSystemMetadata(Message e, MessageResponse rp);
}
