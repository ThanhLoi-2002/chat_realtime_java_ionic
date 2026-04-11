package com.zalo.modules.message.service;

import com.zalo.modules.message.dto.request.CreateSystemMessageRequest;

public interface SystemMessageInterface {
    void createSystemMessage(CreateSystemMessageRequest dto);
}
