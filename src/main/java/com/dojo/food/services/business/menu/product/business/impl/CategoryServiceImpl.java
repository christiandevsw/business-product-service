package com.dojo.food.services.business.menu.product.business.impl;

import com.dojo.food.services.business.menu.product.business.CategoryService;
import com.dojo.food.services.business.menu.product.business.UploadFileService;
import com.dojo.food.services.business.menu.product.business.other.impl.CategoryConvertService;
import com.dojo.food.services.business.menu.product.business.repository.CategoryRepository;
import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import com.dojo.food.services.business.menu.product.model.entity.Category;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryConvertService serviceConvert;
    private final UploadFileService uploadFileService;
    private final Environment env;
    private final String IMG_DIRECTORY;


    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryConvertService serviceConvert,
                               UploadFileService uploadFileService, Environment env) {
        this.categoryRepository = categoryRepository;
        this.serviceConvert = serviceConvert;
        this.uploadFileService = uploadFileService;
        this.env = env;
        IMG_DIRECTORY = env.getProperty("directory.photo.category");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> listCategories() {
        return categoryRepository.findAll().stream().map(serviceConvert::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getById(Long id) {
        Optional<Category> optional = categoryRepository.findById(id);
        return optional.map(serviceConvert::convertToDto).orElse(null);
    }

    @Transactional
    public CategoryDTO create(CategoryDTO dto, MultipartFile file) {
        if (!file.isEmpty()) dto.setPhoto(uploadFileService.getUniqueFileName(file));
        Category newCategory = categoryRepository.save(serviceConvert.convertToEntity(dto));
        if (!file.isEmpty()) {
            try {
                Path absolutePath = Paths.get(IMG_DIRECTORY).resolve(dto.getPhoto()).toAbsolutePath();
                Files.copy(file.getInputStream(), absolutePath);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar en la BBDD. No se pudo guardar su foto");
            }
        }
        return serviceConvert.convertToDto(newCategory);
    }

    @Override
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO categoryDTO, MultipartFile file) {
        Optional<Category> optional = categoryRepository.findById(id);
        if (optional.isEmpty()) return null;

        String filename=optional.get().getPhoto();
        Category currentCategory = optional.get();
        currentCategory.setName(categoryDTO.getName());
        currentCategory.setDescription(categoryDTO.getDescription());
        currentCategory.setPhoto(categoryDTO.getPhoto());
        if (!file.isEmpty()) {
            categoryDTO.setPhoto(uploadFileService.getUniqueFileName(file));
            currentCategory.setPhoto(categoryDTO.getPhoto());
        }

        Category updatedCategory = categoryRepository.save(currentCategory);

        if (!file.isEmpty()) {
            try {
                uploadFileService.copy(IMG_DIRECTORY, categoryDTO.getPhoto(), file);
            } catch (IOException e) {
                throw new RuntimeException("Error al actualizar categoria en la BBDD.No se pudo actualizar su foto");
            }
        }

        if (filename != null && !filename.isEmpty()) {
            if (!uploadFileService.delete(IMG_DIRECTORY, filename))
                throw new RuntimeException("Error al actualizar categoria en la BBDD.No se pudo eliminar la foto anterior");
        }

        return serviceConvert.convertToDto(updatedCategory);
    }

    @Override
    @Transactional
    public void delete(CategoryDTO categoryDTO) {
        categoryRepository.deleteById(categoryDTO.getId());
        if (categoryDTO.getPhoto() != null && !categoryDTO.getPhoto().isEmpty())
            if (!uploadFileService.delete(IMG_DIRECTORY, categoryDTO.getPhoto()) &&
                    uploadFileService.verifyExistFile(IMG_DIRECTORY, categoryDTO.getPhoto()))
                throw new RuntimeException("Error al eliminar la categoria en la BBDD, no se pudo eliminar su foto");
    }

    @Override
    public Resource getImage(CategoryDTO categoryDTO) throws MalformedURLException {
        System.out.println(categoryDTO.getPhoto());
        return uploadFileService.load(IMG_DIRECTORY, categoryDTO.getPhoto());
    }


}
