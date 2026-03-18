package com.zalo.mapper;

import com.zalo.dto.request.Lang.LangCreationRequest;
import com.zalo.dto.request.Lang.LangUpdateRequest;
import com.zalo.dto.response.Lang.LangResponse;
import com.zalo.model.Lang;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LangMapper {
    Lang toEnity(LangCreationRequest request);

    LangResponse toResponse(Lang e);

//    @Mapping(target = "roles", ignore = true)
    void updateDtoToEntity(@MappingTarget Lang e, LangUpdateRequest request);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<LangResponse> toListResponses(List<Lang> entities);
}
