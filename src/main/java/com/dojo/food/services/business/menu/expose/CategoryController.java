package com.dojo.food.services.business.menu.expose;

import com.dojo.food.services.business.menu.product.business.CategoryService;
import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("categories")
public class CategoryController {
    private CategoryService categoryService;

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

    @GetMapping("/{uniqueIdentifier}")
    public ResponseEntity<?> getCategory(@PathVariable String uniqueIdentifier) {
        CategoryDTO dto;
        try {
            dto = categoryService.getById(uniqueIdentifier);
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


    @GetMapping("/uploads/img/{uniqueIdentifier}")
    public ResponseEntity<?> showPhoto(@PathVariable String uniqueIdentifier) {
        CategoryDTO dto;
        try {
            dto = categoryService.getById(uniqueIdentifier);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (dto == null || dto.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        Resource imagen = new ByteArrayResource(dto.getPhoto());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imagen);
    }


    @PostMapping
    public ResponseEntity<?> createNewCategory(@Valid CategoryDTO categoryDto, BindingResult result, @RequestPart MultipartFile file) {
        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<Map<String, Object>>(mistakes, HttpStatus.BAD_REQUEST);
        }

        if (!file.isEmpty()) {
            try {
                categoryDto.setPhoto(file.getBytes());
            } catch (IOException e) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", "Ocurrió un error al seleccionar la foto");
                map.put("error", e.getCause().getMessage());
                return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
            }
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

    @PutMapping("/{uniqueIdentifier}")
    public ResponseEntity<?> updateCurrentCategory(@Valid CategoryDTO categoryDTO, BindingResult result,
                                                   @PathVariable String uniqueIdentifier, @RequestParam MultipartFile file) {
        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<Map<String, Object>>(mistakes, HttpStatus.BAD_REQUEST);
        }

        if (!file.isEmpty()) {
            try {
                categoryDTO.setPhoto(file.getBytes());
            } catch (IOException e) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", "Ocurrió un error al seleccionar la foto");
                map.put("error", e.getCause().getMessage());
                return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
            }
        }

        CategoryDTO dto;
        try {
            dto = categoryService.update(categoryDTO, uniqueIdentifier);
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

    @DeleteMapping("/{uniqueIdentifier}")
    public ResponseEntity<?> deleteCategory(@PathVariable String uniqueIdentifier) {
        try {
            categoryService.deleteById(uniqueIdentifier);
            return new ResponseEntity<String>("Se eliminó correctamente la categoria con id: " + uniqueIdentifier, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
