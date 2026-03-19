package com.zalo.mapper;

import com.zalo.dto.response.User.UserResponse;
import com.zalo.dto.response.conversation.ConversationResponse;
import com.zalo.model.Conversation;
import com.zalo.model.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    ConversationResponse toResponse(Conversation e);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<ConversationResponse> toListResponses(List<Conversation> entities);
}

