package com.dojo.food.services.business.menu.product.business.impl;

import com.dojo.food.services.business.menu.product.business.ProductService;
import com.dojo.food.services.business.menu.product.business.UploadFileService;
import com.dojo.food.services.business.menu.product.business.other.impl.BasicProductConvertService;
import com.dojo.food.services.business.menu.product.business.other.impl.BenefitConvertService;
import com.dojo.food.services.business.menu.product.business.other.impl.DetailProductConvertService;
import com.dojo.food.services.business.menu.product.business.repository.BenefitRepository;
import com.dojo.food.services.business.menu.product.business.repository.CategoryRepository;
import com.dojo.food.services.business.menu.product.business.repository.ProductRepository;
import com.dojo.food.services.business.menu.product.model.dto.BasicProductDTO;
import com.dojo.food.services.business.menu.product.model.dto.BenefitDTO;
import com.dojo.food.services.business.menu.product.model.dto.DetailProductDTO;
import com.dojo.food.services.business.menu.product.model.entity.Benefit;
import com.dojo.food.services.business.menu.product.model.entity.Category;
import com.dojo.food.services.business.menu.product.model.entity.Product;
import com.dojo.food.services.business.menu.product.util.ConstantsService;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BenefitRepository benefitRepository;
    private final BasicProductConvertService basicProductConvert;
    private final DetailProductConvertService detailProductConvert;
    private final BenefitConvertService benefitConvert;
    private final UploadFileService uploadFileService;
    private final Environment env;
    private final String IMG_DIRECTORY;
    private static Long IdSig;


    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
                              BenefitRepository benefitRepository, BasicProductConvertService basicProductConvert,
                              DetailProductConvertService detailProductConvert, BenefitConvertService benefitConvert,
                              UploadFileService uploadFileService, Environment env) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.benefitRepository = benefitRepository;
        this.basicProductConvert = basicProductConvert;
        this.detailProductConvert = detailProductConvert;
        this.benefitConvert = benefitConvert;
        this.uploadFileService = uploadFileService;
        this.env = env;
        IMG_DIRECTORY=env.getProperty("directory.photo.product");
        IdSig= (long) (totalProducts()+1);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer totalProducts() {
        return productRepository.getTotalProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BasicProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(basicProductConvert::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BasicProductDTO> listProductsByCategory(Long categoryId) {
        return productRepository.getProductsByCategory(categoryId).stream()
                .map(basicProductConvert::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BasicProductDTO> getProductsByPrice(BigDecimal price) {
        return productRepository.findByPrice(price.intValue()).stream().map(basicProductConvert::convertToDto
        ).collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<BasicProductDTO> listProductsByNameAndCategory(String name, Long categoryId) {
        return productRepository.findByNameAndCategory(name, categoryId).stream()
                .map(basicProductConvert::convertToDto).collect(Collectors.toList());

    }

    @Override
    public BasicProductDTO getBasicProduct(Long id) {
        Optional<Product> optional = productRepository.findById(id);
        return optional.map(basicProductConvert::convertToDto).orElse(null);
    }

    @Override
    public BasicProductDTO getUniqueProduct(Long id, Map<String, String> headers) {
        Optional<Product> optional = productRepository
                .findByIdAndCategoryId(id, Long.parseLong(headers.get(ConstantsService.CATEGORY) ));

        return optional.map(basicProductConvert::convertToDto).orElse(null);

    }

    @Override
    @Transactional(readOnly = true)
    public DetailProductDTO getDetail(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.map(detailProductConvert::convertToDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public DetailProductDTO retrieveBenefits(DetailProductDTO dto) {
        if (dto.getId() != null) {
            Set<Benefit> benefits = benefitRepository.findByProductId(dto.getId());
            if (!benefits.isEmpty()) {
                Set<BenefitDTO> benefitsDTO = benefits.stream().map(benefitConvert::convertToDto).collect(Collectors.toSet());
                dto.setBenefits(benefitsDTO);
            }
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> verifyProductIfExistsByIdentifier(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Map<String, Object> map = new HashMap<>();
            map.put("product", basicProductConvert.convertToDto(optionalProduct.get()));
            map.put("availability", optionalProduct.get().getAvailable());
            return map;
        }
        return null;
    }

    @Override
    @Transactional
    public DetailProductDTO create(DetailProductDTO dto, MultipartFile file) {
        if (dto.getCategory() == null) return null;
        Optional<Category> optional = categoryRepository.findById(dto.getCategory().getId());
        if (optional.isEmpty()) return null;

        if (dto.getDscto() == null) dto.setDscto(BigDecimal.ZERO);

        if (!file.isEmpty()) dto.setPhoto(uploadFileService.getFileNameBasedOnId(IdSig,file));

        Product product = detailProductConvert.convertToEntity(dto);
        product.setId(IdSig);
        product.setCategory(optional.get());

        product = productRepository.save(product);
        if (!file.isEmpty()) {
            try {
                Path absolutePath = Paths.get(IMG_DIRECTORY).resolve(dto.getPhoto()).toAbsolutePath();
                Files.copy(file.getInputStream(), absolutePath);
            } catch (IOException e) {
                throw new RuntimeException("Se creo el producto, pero no se guardo su foto");
            }
        }

        IdSig++;
        return detailProductConvert.convertToDto(product);
    }

    @Override
    @Transactional
    public DetailProductDTO update(Long id, DetailProductDTO dto, MultipartFile file) {
        if (dto.getCategory() == null) return null;
        Optional<Category> optionalCategory = categoryRepository.findById(dto.getCategory().getId());
        if (optionalCategory.isEmpty()) return null;

        Optional<Product> optional = productRepository.findById(id);
        if (optional.isEmpty()) return null;

        String filename=optional.get().getPhoto();
        Product currentProduct = optional.get();
        currentProduct.setName(dto.getName());
        currentProduct.setPrice(dto.getPrice());
        currentProduct.setDescription(dto.getDescription());
        currentProduct.setStock(dto.getStock());
        currentProduct.setAvailable(dto.getAvailable());
        currentProduct.setCategory(optionalCategory.get());
        currentProduct.setDscto(dto.getDscto());
        currentProduct.setPhoto(dto.getPhoto());
        if (!file.isEmpty()) {
            dto.setPhoto(uploadFileService.getFileNameBasedOnId(id,file));
            currentProduct.setPhoto(dto.getPhoto());
        }

        currentProduct = productRepository.save(currentProduct);

        if (!file.isEmpty()) {
            try {
                uploadFileService.copy(IMG_DIRECTORY, dto.getPhoto(), file);
            } catch (IOException e) {
                throw new RuntimeException("Error al actualizar producto en la BBDD. No se pudo actualizar la foto");
            }
        }

        if (filename != null && !filename.isEmpty()) {
            if (!uploadFileService.delete(IMG_DIRECTORY, filename))
                throw new RuntimeException("Se actualizo el producto pero no se pudo eliminar la foto anterior");
        }


        return detailProductConvert.convertToDto(currentProduct);
    }

    @Override
    @Transactional
    public void delete(BasicProductDTO dto) {
        productRepository.deleteById(dto.getId());
        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty())
            if (!uploadFileService.delete(IMG_DIRECTORY, dto.getPhoto()) &&
                    uploadFileService.verifyExistFile(IMG_DIRECTORY, dto.getPhoto()))
                throw new RuntimeException("Error al eliminar el producto en la BBDD, no se pudo eliminar su foto");
    }

    @Override
    public Resource getImage(BasicProductDTO dto) throws MalformedURLException {
        return uploadFileService.load(IMG_DIRECTORY, dto.getPhoto());
    }

}
