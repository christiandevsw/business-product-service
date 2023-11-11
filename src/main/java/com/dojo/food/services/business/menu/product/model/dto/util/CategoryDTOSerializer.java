package com.dojo.food.services.business.menu.product.model.dto.util;

import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CategoryDTOSerializer extends JsonSerializer<CategoryDTO> {
    @Override
    public void serialize(CategoryDTO dto, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(dto.getName());
    }
}
