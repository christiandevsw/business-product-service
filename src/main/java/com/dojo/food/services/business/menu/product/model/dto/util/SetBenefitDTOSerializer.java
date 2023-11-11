package com.dojo.food.services.business.menu.product.model.dto.util;

import com.dojo.food.services.business.menu.product.model.dto.BenefitDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Set;

public class SetBenefitDTOSerializer extends JsonSerializer<Set<BenefitDTO>> {

    @Override
    public void serialize(Set<BenefitDTO> setBenefitDTO, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (BenefitDTO e : setBenefitDTO)
            gen.writeString(e.getDescription());
        gen.writeEndArray();
    }
}
