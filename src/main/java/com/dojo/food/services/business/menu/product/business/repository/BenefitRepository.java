package com.dojo.food.services.business.menu.product.business.repository;


import com.dojo.food.services.business.menu.product.model.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    Set<Benefit> findByProductId(Long id);

    Optional<Benefit> findByUniqueIdentifier(String uniqueIdentifier);

    Optional<Benefit> findByUniqueIdentifierAndProductUniqueIdentifierAndProductCategoryUniqueIdentifier(String id, String ProductId, String CategoryId);
}
