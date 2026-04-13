package com.zalo.modules.conversation.service;

import java.util.List;
import java.util.Map;

public interface ConversationInterface {
    Map<Long, Integer> isMentionFromIds(List<Long> ids, Long userId);
}
