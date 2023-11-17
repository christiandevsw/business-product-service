package com.dojo.food.services.business.menu.expose;

import com.dojo.food.services.business.menu.product.business.CategoryService;
import com.dojo.food.services.business.menu.product.business.ProductService;
import com.dojo.food.services.business.menu.product.model.dto.BasicProductDTO;
import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import com.dojo.food.services.business.menu.product.model.dto.DetailProductDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ProductController {
    private ProductService productService;
    private CategoryService categoryService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/total-products")
    public ResponseEntity<?> totalProducts() {
        try {
            return new ResponseEntity<String>(String.format("el total de productos es %d", productService.totalProducts()), HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    @GetMapping("/list-by-category/{identifierCategory}")
    public ResponseEntity<?> listProductsByCategory(@PathVariable String identifierCategory) {
        CategoryDTO categoryDTO;
        try {
            categoryDTO = categoryService.getById(identifierCategory);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (categoryDTO == null)
            return new ResponseEntity<String>("No existe una categoria en la BBDD para el id: ".concat(identifierCategory.toString()), HttpStatus.OK);

        return new ResponseEntity<List<BasicProductDTO>>(productService.listProductsByCategory(identifierCategory), HttpStatus.OK);
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
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (productsDtos.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron productos para este precio");
        }
        return new ResponseEntity<List<BasicProductDTO>>(productsDtos, HttpStatus.OK);
    }

    @GetMapping("find/{categoryIdentifier}/{name}")
    public ResponseEntity<?> findProductByCategoryAndName(@PathVariable String categoryIdentifier, @PathVariable String name) {
        List<BasicProductDTO> productsDtos;
        try {
            productsDtos = productService.listProductsByNameAndCategory(name, categoryIdentifier);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (productsDtos.size() == 0)
            return new ResponseEntity<String>("No se encontraron productos en la BBDD!", HttpStatus.NOT_FOUND);

        return new ResponseEntity<List<BasicProductDTO>>(productsDtos, HttpStatus.OK);
    }

    @GetMapping("/{uniqueIdentifier}")
    public ResponseEntity<?> getProduct(@PathVariable String uniqueIdentifier) {
        DetailProductDTO detailProductDTO;
        try {
            detailProductDTO = productService.getProductByIdentifier(uniqueIdentifier);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (detailProductDTO == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el producto en BBDD");


        detailProductDTO = productService.retrieveBenefits(detailProductDTO);
        return new ResponseEntity<DetailProductDTO>(detailProductDTO, HttpStatus.OK);
    }

    @GetMapping("/uploads/img/{uniqueIdentifier}")
    public ResponseEntity<?> showPhoto(@PathVariable String uniqueIdentifier) {
        DetailProductDTO dto;
        try {
            dto = productService.getProductByIdentifier(uniqueIdentifier);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (dto == null || dto.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        Resource imagen = new ByteArrayResource(dto.getPhoto());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imagen);
    }

    @GetMapping("/available/{uniqueIdentifier}")
    public ResponseEntity<?> verifyStatusToProduct(@PathVariable String uniqueIdentifier) {
        Map<String, Object> response;
        try {
            response = productService.verifyProductIfExistsByIdentifier(uniqueIdentifier);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response == null) {
            return new ResponseEntity<String>("No existe el producto en la BBDD!", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/new-product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createNewProduct(@Valid @RequestBody DetailProductDTO detailProductDTO, BindingResult result
                                              ) {
        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<Map<String, Object>>(mistakes, HttpStatus.BAD_REQUEST);
        }

        DetailProductDTO newProductDto;
        try {
            newProductDto = productService.create(detailProductDTO);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (newProductDto == null)
            return new ResponseEntity<String>("La categoria a la que pertenece el producto no existe en la BBDD", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<DetailProductDTO>(newProductDto, HttpStatus.CREATED);
    }

    @PatchMapping("/update-product/{uniqueIdentifier}")
    public ResponseEntity<?> updateCurrentProduct(@Valid @RequestBody DetailProductDTO detailProductDTO, BindingResult result,
                                                  @PathVariable String uniqueIdentifier) {

        if (result.hasErrors()) {
            Map<String, Object> mistakes = new HashMap<>();
            result.getFieldErrors().forEach(error -> mistakes.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage()));
            return new ResponseEntity<Map<String, Object>>(mistakes, HttpStatus.BAD_REQUEST);
        }

        DetailProductDTO dto;
        try {
            dto = productService.update(detailProductDTO, uniqueIdentifier);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (dto == null) {
            return new ResponseEntity<String>("No existe el producto en la BBDD ", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<DetailProductDTO>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-product/{uniqueIdentifier}")
    public ResponseEntity<?> deleteProduct(@PathVariable String uniqueIdentifier, @RequestHeader Map<String, String> headers) {
        BasicProductDTO basicProductDTO;
        try {
            basicProductDTO = productService.deleteProduct(uniqueIdentifier, headers);
        } catch (DataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMostSpecificCause().getMessage());
            map.put("message", "Ocurrió un error en la BBDD");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (basicProductDTO == null)
            return new ResponseEntity<String>("No existe el producto en la BBDD", HttpStatus.NOT_FOUND);
        return new ResponseEntity<String>("Se eliminó correctamente el producto con id: " + uniqueIdentifier, HttpStatus.OK);
    }

}
