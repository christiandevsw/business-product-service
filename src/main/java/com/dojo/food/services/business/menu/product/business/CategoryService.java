package com.dojo.food.services.business.menu.product.business;

import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

public interface CategoryService {
    List<CategoryDTO> listCategories();

    CategoryDTO getById(Long id);

    CategoryDTO create(CategoryDTO categoryDTO,MultipartFile file);

    CategoryDTO update(Long id, CategoryDTO categoryDTO, MultipartFile file);

    void delete(CategoryDTO categoryDTO);

    Resource getImage(CategoryDTO categoryDTO) throws MalformedURLException ;

}
