package com.zalo.model.covert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.model.File;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class FileConverter implements AttributeConverter<File, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(File avatar) {
        try {
            return objectMapper.writeValueAsString(avatar);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public File convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, File.class);
        } catch (Exception e) {
            return null;
        }
    }
}
