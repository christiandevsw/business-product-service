package com.dojo.food.services.business.menu.product.business;

import com.dojo.food.services.business.menu.product.model.dto.BasicProductDTO;
import com.dojo.food.services.business.menu.product.model.dto.DetailProductDTO;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    Integer totalProducts();

    List<BasicProductDTO> getAllProducts();

    List<BasicProductDTO> listProductsByCategory(Long categoryId);

    List<BasicProductDTO> getProductsByPrice(BigDecimal price);

    List<BasicProductDTO> listProductsByNameAndCategory(String name, Long categoryId);

    DetailProductDTO getProduct(Long id);

    DetailProductDTO retrieveBenefits(DetailProductDTO dto);

    Map<String, Object> verifyProductIfExistsByIdentifier(Long id);

    DetailProductDTO create(DetailProductDTO detailProductDTO);

    DetailProductDTO update(DetailProductDTO detailProductDTO, Long id) throws DataAccessException;

    BasicProductDTO deleteProduct(Long id, Map<String, Long> headers);

}
