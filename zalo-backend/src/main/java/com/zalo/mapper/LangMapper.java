package com.zalo.mapper;

import com.zalo.dto.request.Lang.LangCreationRequest;
import com.zalo.dto.request.Lang.LangUpdateRequest;
import com.zalo.dto.response.Lang.LangResponse;
import com.zalo.model.Lang;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface LangMapper {
    Lang toEnity(LangCreationRequest request);

    @Named("full")
    @Mapping(target = "createdBy", qualifiedByName = "userSafe")
    @Mapping(target = "updatedBy", qualifiedByName = "userSafe")
    LangResponse toResponse(Lang e);

    void updateDtoToEntity(@MappingTarget Lang e, LangUpdateRequest request);

    @IterableMapping(qualifiedByName = "full", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<LangResponse> toListResponses(List<Lang> entities);
}
