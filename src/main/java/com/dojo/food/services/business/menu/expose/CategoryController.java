package com.dojo.food.services.business.menu.expose;

import com.dojo.food.services.business.menu.product.business.CategoryService;
import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("categories")
@RefreshScope
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getCategories() {
        try {
            return new ResponseEntity<>(categoryService.listCategories(), HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BD");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe la categoria");
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @GetMapping("/uploads/img/{id}")
    public ResponseEntity<?> showPhoto(@PathVariable Long id) {
        CategoryDTO categoryDTO = categoryService.getById(id);
        if (categoryDTO == null || categoryDTO.getPhoto() == null) return ResponseEntity.notFound().build();

        Resource img;

        try {
            img = categoryService.getImage(categoryDTO);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(img);
        } catch (MalformedURLException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createNewCategory(@Valid CategoryDTO categoryDto, BindingResult result,
                                               @RequestPart MultipartFile file) {
        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<>(mistakes, HttpStatus.BAD_REQUEST);
        }

        try {
            CategoryDTO categoryDTO = categoryService.create(categoryDto, file);
            return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCurrentCategory(@Valid CategoryDTO categoryDTO, BindingResult result,
                                                   @RequestPart MultipartFile file,
                                                   @PathVariable Long id) {
        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<>(mistakes, HttpStatus.BAD_REQUEST);
        }

        CategoryDTO dto;
        try {
            dto = categoryService.update(id, categoryDTO, file);
        } catch (RuntimeException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (dto == null) {
            return new ResponseEntity<>("No existe categoria en la BD", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        CategoryDTO categoryDTO;
        try {
            categoryDTO = categoryService.getById(id);
            if (categoryDTO != null) {
                categoryService.delete(categoryDTO);
                return new ResponseEntity<>("Se eliminó correctamente la categoria", HttpStatus.OK);
            }

        } catch (RuntimeException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("No existe la categoria en la BBDD", HttpStatus.NOT_FOUND);
    }


}
