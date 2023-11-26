package com.dojo.food.services.business.menu.product.business.other.impl;

import com.dojo.food.services.business.menu.product.business.other.ConvertService;
import com.dojo.food.services.business.menu.product.model.dto.BenefitDTO;
import com.dojo.food.services.business.menu.product.model.entity.Benefit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BenefitConvertService implements ConvertService<Benefit, BenefitDTO> {
    private DetailProductConvertService detailProductConvert;

    @Override
    public Benefit convertToEntity(BenefitDTO t) {
        if (t != null) {
            Benefit benefit = new Benefit();
            benefit.setId(t.getId());
            benefit.setDescription(t.getDescription());
            benefit.setProduct(detailProductConvert.convertToEntity(t.getProduct()));
            return benefit;
        }
        return null;
    }

    @Override
    public BenefitDTO convertToDto(Benefit t) {
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setId(t.getId());
        benefitDTO.setDescription(t.getDescription());
        benefitDTO.setProduct(detailProductConvert.convertToDto(t.getProduct()));
        return benefitDTO;
    }
}
