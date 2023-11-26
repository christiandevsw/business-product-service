package com.dojo.food.services.business.menu.product.business.impl;

import com.dojo.food.services.business.menu.product.business.BenefitService;
import com.dojo.food.services.business.menu.product.business.other.impl.BenefitConvertService;
import com.dojo.food.services.business.menu.product.business.repository.BenefitRepository;
import com.dojo.food.services.business.menu.product.business.repository.CategoryRepository;
import com.dojo.food.services.business.menu.product.business.repository.ProductRepository;
import com.dojo.food.services.business.menu.product.model.dto.BenefitDTO;
import com.dojo.food.services.business.menu.product.model.dto.DetailProductDTO;
import com.dojo.food.services.business.menu.product.model.entity.Benefit;
import com.dojo.food.services.business.menu.product.model.entity.Category;
import com.dojo.food.services.business.menu.product.model.entity.Product;
import com.dojo.food.services.business.menu.product.util.ConstantsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BenefitServiceImpl implements BenefitService {
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private BenefitRepository benefitRepository;
    private BenefitConvertService benefitConvert;

    @Override
    @Transactional
    public BenefitDTO save(BenefitDTO benefitDTO) {
        DetailProductDTO detailProductDTO = benefitDTO.getProduct();
        Optional<Category> optionalCategory = categoryRepository.findById(detailProductDTO.getCategory().getId());
        if (optionalCategory.isEmpty()) return null;

        Optional<Product> optionalProduct = productRepository.findById(detailProductDTO.getId());
        if (optionalProduct.isEmpty()) return null;

        Benefit newBenefit = benefitConvert.convertToEntity(benefitDTO);
        newBenefit.setProduct(optionalProduct.get());
        return benefitConvert.convertToDto(benefitRepository.save(newBenefit));
    }

    @Override
    @Transactional
    public BenefitDTO update(Long id, BenefitDTO benefitDTO) {
        DetailProductDTO detailProductDTO = benefitDTO.getProduct();
        Optional<Category> optionalCategory = categoryRepository.findById(detailProductDTO.getCategory().getId());
        if (optionalCategory.isEmpty()) return null;

        Optional<Product> optionalProduct = productRepository.findById(detailProductDTO.getId());
        if (optionalProduct.isEmpty()) return null;

        Optional<Benefit> optionalBenefit = benefitRepository.findById(id);

        if (optionalBenefit.isEmpty()) return null;
        Benefit currentBenefit = optionalBenefit.get();
        currentBenefit.setDescription(benefitDTO.getDescription());
        currentBenefit.setProduct(optionalProduct.get());
        return benefitConvert.convertToDto(currentBenefit);
    }


    @Override
    @Transactional
    public BenefitDTO delete(Long id, Map<String, Long> headers) {
        Optional<Benefit> optional = benefitRepository
                .findByIdAndProductIdAndProductCategoryId(id,
                        headers.get(ConstantsService.PRODUCT), headers.get(ConstantsService.CATEGORY));

        if (optional.isPresent()) {
            benefitRepository.delete(optional.get());
            return benefitConvert.convertToDto(optional.get());
        }
        return null;
    }

}
