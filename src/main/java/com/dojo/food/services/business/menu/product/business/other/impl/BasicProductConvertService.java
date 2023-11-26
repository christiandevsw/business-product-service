package com.dojo.food.services.business.menu.product.business.other.impl;

import com.dojo.food.services.business.menu.product.business.other.ConvertService;
import com.dojo.food.services.business.menu.product.model.dto.BasicProductDTO;
import com.dojo.food.services.business.menu.product.model.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class BasicProductConvertService implements ConvertService<Product, BasicProductDTO> {
    @Override
    public Product convertToEntity(BasicProductDTO t) {
        if (t != null) {
            Product product = new Product();
            product.setId(t.getId());
            product.setName(t.getName());
            product.setDescription(t.getDescription());
            product.setPrice(t.getPrice());
            product.setDscto(t.getDscto());

            return product;

        }
        return null;
    }

    @Override
    public BasicProductDTO convertToDto(Product t) {
        BasicProductDTO basicProductDTO = new BasicProductDTO();
        basicProductDTO.setId(t.getId());
        basicProductDTO.setName(t.getName());
        basicProductDTO.setPrice(t.getPrice());
        basicProductDTO.setDscto(t.getDscto());
        basicProductDTO.setDescription(t.getDescription());
        return basicProductDTO;
    }
}
