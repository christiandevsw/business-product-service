package com.dojo.food.services.business.menu.product.business;

import com.dojo.food.services.business.menu.product.model.dto.BasicProductDTO;
import com.dojo.food.services.business.menu.product.model.dto.CategoryDTO;
import com.dojo.food.services.business.menu.product.model.dto.DetailProductDTO;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    Integer totalProducts();

    List<BasicProductDTO> getAllProducts();

    List<BasicProductDTO> listProductsByCategory(Long categoryId);

    List<BasicProductDTO> getProductsByPrice(BigDecimal price);

    List<BasicProductDTO> listProductsByNameAndCategory(String name, Long categoryId);

    BasicProductDTO getBasicProduct(Long id);

    BasicProductDTO getUniqueProduct(Long id, Map<String,String> headers);

    DetailProductDTO getDetail(Long id);

    DetailProductDTO retrieveBenefits(DetailProductDTO dto);

    Map<String, Object> verifyProductIfExistsByIdentifier(Long id);

    DetailProductDTO create(DetailProductDTO detailProductDTO, MultipartFile file);

    DetailProductDTO update(Long id,DetailProductDTO detailProductDTO, MultipartFile file);

    void delete(BasicProductDTO dto);

    Resource getImage(BasicProductDTO dto) throws MalformedURLException;

}
