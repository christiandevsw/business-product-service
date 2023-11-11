package com.dojo.food.services.business.menu.product.model.components;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AbstractProductResponse {
    private String rqUUID;
    private String resultCode;
    private String resultDescription;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private Date operationDate;
}
