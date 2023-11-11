package com.dojo.food.services.business.menu.product.model.components;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ProductHeader {

    private String authorization;
    private String requestId;
    private String requestDate;
    private String callerName;


}
