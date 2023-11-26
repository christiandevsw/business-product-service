package com.dojo.food.services.business.menu.product.business;

import com.dojo.food.services.business.menu.product.model.dto.BenefitDTO;

import java.util.Map;

public interface BenefitService {
    BenefitDTO save(BenefitDTO benefitDTO);

    BenefitDTO update(Long id, BenefitDTO benefitDTO);

    BenefitDTO delete(Long id, Map<String, Long> headers);
}
