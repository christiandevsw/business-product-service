package com.dojo.food.services.business.menu.product.business.repository;


import com.dojo.food.services.business.menu.product.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByUniqueIdentifier(String id);
}
