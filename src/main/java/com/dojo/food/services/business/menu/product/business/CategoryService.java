package com.dojo.food.services.business.menu.product.business;

import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> listCategories();

    CategoryDTO getById(String id);

    CategoryDTO create(CategoryDTO categoryDTO);

    CategoryDTO update(CategoryDTO categoryDTO, String id) throws DataAccessException;

    void deleteById(String id);

}
