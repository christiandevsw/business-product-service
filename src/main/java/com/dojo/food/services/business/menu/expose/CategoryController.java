package com.dojo.food.services.business.menu.expose;

import com.dojo.food.services.business.menu.product.business.CategoryService;
import com.dojo.food.services.business.menu.product.business.UploadFileService;
import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("categories")
@RefreshScope
public class CategoryController {
    private CategoryService categoryService;
    private UploadFileService uploadFileService;
    private Environment env;
    private final String IMG_DIRECTORY = env.getProperty("directory.photo.category");

    public CategoryController(CategoryService categoryService, UploadFileService uploadFileService, Environment env) {
        this.categoryService = categoryService;
        this.uploadFileService = uploadFileService;
        this.env = env;
    }

    @GetMapping
    public ResponseEntity<?> getCategories() {
        try {
            return new ResponseEntity<List<CategoryDTO>>(categoryService.listCategories(), HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable Long id) {
        CategoryDTO dto;
        try {
            dto = categoryService.getById(id);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe la categoria");
        }

        return new ResponseEntity<CategoryDTO>(dto, HttpStatus.OK);
    }


    @GetMapping("/uploads/img/{id}")
    public ResponseEntity<Resource> showPhoto(@PathVariable Long id) {
        CategoryDTO categoryDTO = categoryService.getById(id);
        if (categoryDTO == null || categoryDTO.getPhoto() == null) return ResponseEntity.notFound().build();

        try {
            Resource resource = uploadFileService.load(IMG_DIRECTORY, categoryDTO.getPhoto());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping
    public ResponseEntity<?> createNewCategory(@Valid CategoryDTO categoryDto, BindingResult result,
                                               @RequestPart MultipartFile file) {
        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<Map<String, Object>>(mistakes, HttpStatus.BAD_REQUEST);
        }

        if (!file.isEmpty()) {
            categoryDto.setPhoto(uniqueFileName);
        }

        try {
            CategoryDTO categoryDTO = categoryService.create(categoryDto);
            return new ResponseEntity<CategoryDTO>(categoryDTO, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCurrentCategory(@Valid CategoryDTO categoryDTO, BindingResult result,
                                                   @RequestPart MultipartFile file,
                                                   @PathVariable Long id) {
        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<Map<String, Object>>(mistakes, HttpStatus.BAD_REQUEST);
        }

        String uniqueFileName = null;
        if (!file.isEmpty()) {
            if (categoryDTO.getPhoto() != null)
                uploadFileService.delete(IMG_DIRECTORY, categoryDTO.getPhoto());

            try {
                uniqueFileName = uploadFileService.copy(IMG_DIRECTORY, file);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        categoryDTO.setPhoto(uniqueFileName);
        if (categoryDTO.getPhoto() == null)
            uploadFileService.delete(IMG_DIRECTORY, categoryDTO.getPhoto());

        CategoryDTO dto;
        try {
            dto = categoryService.update(categoryDTO, id);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (dto == null) {
            return new ResponseEntity<String>("No existe categoria en la BD", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<CategoryDTO>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            CategoryDTO categoryDTO = categoryService.getById(id);
            categoryService.deleteById(id);

            if (categoryDTO.getPhoto() != null) {


            }
            return new ResponseEntity<String>("Se eliminó correctamente la categoria", HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
