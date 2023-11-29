package com.dojo.food.services.business.menu.expose;

import com.dojo.food.services.business.menu.product.business.CategoryService;
import com.dojo.food.services.business.menu.product.business.ProductService;
import com.dojo.food.services.business.menu.product.model.dto.BasicProductDTO;
import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import com.dojo.food.services.business.menu.product.model.dto.DetailProductDTO;
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

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RefreshScope
public class ProductController {
    private ProductService productService;
    private CategoryService categoryService;

    @GetMapping("/total-products")
    public ResponseEntity<?> totalProducts() {
        try {
            return new ResponseEntity<>(String.format("el total de productos es %d", productService.totalProducts()), HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        List<BasicProductDTO> productDTOS;
        try {
            productDTOS = productService.getAllProducts();
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Error al recuperar los productos de la BBDD");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    @GetMapping("/list-by-category/{categoryId}")
    public ResponseEntity<?> listProductsByCategory(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO;
        try {
            categoryDTO = categoryService.getById(categoryId);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (categoryDTO == null)
            return new ResponseEntity<>("No existe una categoria en la BBDD", HttpStatus.OK);

        return new ResponseEntity<>(productService.listProductsByCategory(categoryId), HttpStatus.OK);
    }

    @GetMapping("/list-by-price/{price}")
    public ResponseEntity<?> listByPrice(@PathVariable BigDecimal price) {
        List<BasicProductDTO> productsDtos;
        try {
            productsDtos = productService.getProductsByPrice(price);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (productsDtos.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron productos para este precio");
        }
        return new ResponseEntity<>(productsDtos, HttpStatus.OK);
    }

    @GetMapping("find/{categoryId}/{name}")
    public ResponseEntity<?> findProductByCategoryAndName(@PathVariable Long categoryId, @PathVariable String name) {
        List<BasicProductDTO> productsDtos;
        try {
            productsDtos = productService.listProductsByNameAndCategory(name, categoryId);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (productsDtos.size() == 0)
            return new ResponseEntity<>("No se encontraron productos en la BBDD!", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(productsDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        DetailProductDTO detailProductDTO;
        try {
            detailProductDTO = productService.getDetail(id);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (detailProductDTO == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el producto en BBDD");


        detailProductDTO = productService.retrieveBenefits(detailProductDTO);
        return new ResponseEntity<>(detailProductDTO, HttpStatus.OK);
    }


    @GetMapping("/uploads/img/{id}")
    public ResponseEntity<?> showPhoto(@PathVariable Long id) {
        BasicProductDTO dto = productService.getBasicProduct(id);
        if (dto == null || dto.getPhoto() == null) return ResponseEntity.notFound().build();

        Resource img;

        try {
            img = productService.getImage(dto);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(img);
        } catch (MalformedURLException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available/{id}")
    public ResponseEntity<?> verifyStatusToProduct(@PathVariable Long id) {
        Map<String, Object> response;
        try {
            response = productService.verifyProductIfExistsByIdentifier(id);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response == null) {
            return new ResponseEntity<>("No existe el producto en la BBDD!", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = "/new-product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createNewProduct(@Valid DetailProductDTO detailProductDTO, BindingResult result,
                                              @RequestPart MultipartFile file) {

        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<>(mistakes, HttpStatus.BAD_REQUEST);
        }

        DetailProductDTO dto;
        try {
            dto = productService.create(detailProductDTO, file);
        } catch (RuntimeException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (dto == null)
            return new ResponseEntity<>("La categoria a la que pertenece el producto no existe en la BBDD", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PatchMapping("/update-product/{id}")
    public ResponseEntity<?> updateCurrentProduct(@Valid DetailProductDTO detailProductDTO, BindingResult result,
                                                  @RequestPart MultipartFile file, @PathVariable Long id) {

        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<>(mistakes, HttpStatus.BAD_REQUEST);
        }

        DetailProductDTO dto;
        try {
            dto = productService.update(id, detailProductDTO, file);
        } catch (RuntimeException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (dto == null) {
            return new ResponseEntity<>("No se pudo actualizar el producto en la BBDD ", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, @RequestHeader Map<String, String> headers) {
        BasicProductDTO basicProductDTO;

        try {
            basicProductDTO = productService.getUniqueProduct(id,headers);
            if (basicProductDTO!=null){
                productService.delete(basicProductDTO);
                return new ResponseEntity<>("Se eliminó correctamente la categoria", HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("No existe le producto en la BBDD", HttpStatus.NOT_FOUND);
    }

}
