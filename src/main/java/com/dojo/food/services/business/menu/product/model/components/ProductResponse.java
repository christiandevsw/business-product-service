package com.dojo.food.services.business.menu.product.model.components;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductResponse extends  AbstractProductResponse{

    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal dscto;
    private String description;
    private Integer stock;
    private Boolean available;
    private byte[] photo;

}
