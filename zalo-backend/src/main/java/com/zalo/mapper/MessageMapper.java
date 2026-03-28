package com.zalo.mapper;

import com.zalo.dto.response.Message.MessageResponse;
import com.zalo.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Named("fullMessage")
    MessageResponse toResponse(Message e);

    @Named("liteMessage")
    MessageResponse toLite(Message e);

    @Named("safeMessage")
    default MessageResponse mapSafe(Message m) {
        if (m == null || !org.hibernate.Hibernate.isInitialized(m)) {
            return null;
        }
        return toLite(m); // tránh loop
    }
}
