package com.dojo.food.services.business.menu.product.model.dto;

import com.dojo.food.services.business.menu.product.model.dto.util.DetailProductDTOSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
public class BenefitDTO {
    private Long id;
    private String description;
    @JsonSerialize(using = DetailProductDTOSerializer.class)
    private DetailProductDTO product;
}
