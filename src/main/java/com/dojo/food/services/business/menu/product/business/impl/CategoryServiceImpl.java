package com.dojo.food.services.business.menu.product.business.impl;

import com.dojo.food.services.business.menu.product.business.CategoryService;
import com.dojo.food.services.business.menu.product.business.other.impl.CategoryConvertService;
import com.dojo.food.services.business.menu.product.business.repository.CategoryRepository;
import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import com.dojo.food.services.business.menu.product.model.entity.Category;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;
    private CategoryConvertService serviceConvert;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> listCategories() {
        return categoryRepository.findAll().stream().map(serviceConvert::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getById(Long id) {
        Optional<Category> optional = categoryRepository.findById(id);
        if (optional.isPresent()) return serviceConvert.convertToDto(optional.get());
        return null;
    }

    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        Category newCategory = categoryRepository.save(serviceConvert.convertToEntity(dto));
        return serviceConvert.convertToDto(newCategory);
    }

    @Override
    @Transactional
    public CategoryDTO update(CategoryDTO categoryDTO, Long id) throws DataAccessException {
        Optional<Category> optional = categoryRepository.findById(id);
        if (optional.isEmpty()) return null;

        Category currentCategory = optional.get();
        currentCategory.setName(categoryDTO.getName());
        currentCategory.setDescription(categoryDTO.getDescription());
        if (categoryDTO.getPhoto() != null) {
            currentCategory.setPhoto(categoryDTO.getPhoto());
        }
        categoryDTO.setPhoto(null);
        return serviceConvert.convertToDto(categoryRepository.save(currentCategory));

    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }


}
