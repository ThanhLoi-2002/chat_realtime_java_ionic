package com.zalo.mapper;

import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Named("full")
    @Mapping(target = "createdBy", expression = "java(mapSafe(e.getCreatedBy()))")
    @Mapping(target = "updatedBy", expression = "java(mapSafe(e.getUpdatedBy()))")
    UserResponse toResponse(User e);

    @Named("lite")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UserResponse toLite(User e);

    @Named("userSafe")
    default UserResponse mapSafe(User user) {
        if (user == null || !org.hibernate.Hibernate.isInitialized(user)) {
            return null;
        }
        return toLite(user); // 🔥 QUAN TRỌNG: không gọi lại toResponse
    }

    @IterableMapping(
            qualifiedByName = "full",
            nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<UserResponse> toListResponses(List<User> entities);
}
