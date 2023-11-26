package com.dojo.food.services.business.menu.product.model.dto;

import com.dojo.food.services.business.menu.product.model.dto.util.CategoryDTOSerializer;
import com.dojo.food.services.business.menu.product.model.dto.util.SetBenefitDTOSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    @JsonSerialize(using = CategoryDTOSerializer.class)
    private CategoryDTO category;
    private BigDecimal dscto;
    private Integer stock;
    private Boolean available;
    @JsonIgnore
    private String photo;
    @JsonSerialize(using = SetBenefitDTOSerializer.class)
    private Set<BenefitDTO> benefits = new HashSet<>();

}
