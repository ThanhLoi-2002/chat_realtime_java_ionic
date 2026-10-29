package com.zalo.common.covert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.modules.admin.subscriptionPackage.dto.request.GiftDTO;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class JsonGiftConverter implements AttributeConverter<List<GiftDTO>, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<GiftDTO> attribute) {
        if (attribute == null || attribute.isEmpty()) return "[]";
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<GiftDTO> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) return new ArrayList<>();
        try {
            return mapper.readValue(dbData, new TypeReference<List<GiftDTO>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
