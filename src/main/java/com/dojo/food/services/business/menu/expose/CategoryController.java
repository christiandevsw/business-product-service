package com.dojo.food.services.business.menu.expose;

import com.dojo.food.services.business.menu.product.business.CategoryService;
import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@AllArgsConstructor
@RequestMapping("categories")
@RefreshScope
public class CategoryController {
    private CategoryService categoryService;
    private Environment env;

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


//    @GetMapping("/uploads/img/{uniqueIdentifier}")
//    public ResponseEntity<?> showPhoto(@PathVariable String uniqueIdentifier) {
//        CategoryDTO dto;
//        try {
//            dto = categoryService.getById(uniqueIdentifier);
//        } catch (DataAccessException e) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("error", e.getMostSpecificCause().getMessage());
//            map.put("message", "Ocurrió un error en la BD");
//            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        if (dto == null || dto.getPhoto() == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Resource imagen = new ByteArrayResource(dto.getPhoto());
//        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imagen);
//    }

    @GetMapping("/uploads/img/{filename:.+}")
    public ResponseEntity<Resource> showPhoto(@PathVariable String filename){
        Path pathPhoto= Paths.get(env.getProperty("directory.photo.category")).resolve(filename).toAbsolutePath();
        Resource resource=null;
        try {
            resource=new UrlResource(pathPhoto.toUri());
            if (!resource.exists() && !resource.isReadable())
                throw new RuntimeException("Error: no se puede cargar la imagen");
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""+resource.getFilename()+"\"").body(resource);

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
            try {
                String uniqueFileName= UUID.randomUUID().toString()+"_"+file.getOriginalFilename();
                Path pathPhoto=Paths.get(env.getProperty("directory.photo.category")).resolve(uniqueFileName).toAbsolutePath();
                Files.copy(file.getInputStream(),pathPhoto);
                categoryDto.setPhoto(uniqueFileName);
            } catch (IOException e) {
                Map<String, Object> map = new HashMap<>();
                map.put("error", e.getCause().getMessage());
                map.put("message", "Ocurrió un error al asignar la foto seleccionada");
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
    public ResponseEntity<?> updateCurrentCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult result,
                                                   @PathVariable String uniqueIdentifier) {
        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<Map<String, Object>>(mistakes, HttpStatus.BAD_REQUEST);
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
