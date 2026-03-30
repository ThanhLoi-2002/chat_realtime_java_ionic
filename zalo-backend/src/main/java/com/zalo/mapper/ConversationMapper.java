package com.zalo.mapper;

import com.zalo.dto.response.Conversation.ConversationResponse;
import com.zalo.model.Conversation;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, MessageMapper.class})
public interface ConversationMapper {

    @Mapping(target = "lastMessage", qualifiedByName = "safeMessage")
    @Mapping(target = "recipient", qualifiedByName = "userSafe")
    ConversationResponse toResponse(Conversation e);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<ConversationResponse> toListResponses(List<Conversation> entities);
}

