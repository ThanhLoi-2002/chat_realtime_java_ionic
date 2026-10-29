package com.zalo.common.covert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.modules.admin.subscriptionPackage.dto.request.FeatureDTO;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class JsonFeatureConverter implements AttributeConverter<List<FeatureDTO>, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<FeatureDTO> attribute) {
        if (attribute == null || attribute.isEmpty()) return "[]";
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<FeatureDTO> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) return new ArrayList<>();
        try {
            return mapper.readValue(dbData, new TypeReference<List<FeatureDTO>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
