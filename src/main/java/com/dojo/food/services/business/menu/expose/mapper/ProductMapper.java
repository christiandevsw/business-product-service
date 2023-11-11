//package com.dojo.food.services.business.menu.expose.mapper;
//
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import com.dojo.food.services.business.menu.product.model.components.ProductResponse;
//import com.dojo.food.services.business.menu.product.model.dto.ProductDto;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.Mappings;
//import org.mapstruct.NullValueCheckStrategy;
//import org.mapstruct.NullValuePropertyMappingStrategy;
//import org.springframework.stereotype.Component;
//
//
///**
// * <b>Class</b>: ProductMapper.java<br/>
// * <b>Copyright</b>: &copy; 2020 Banco de Cr&eacute;dito del Per&uacute;.<br/>
// * <b>Company</b>: Banco de Cr&eacute;dito del Per&uacute;.<br/>
// *
// * @author Banco de Cr&eacute;dito del Per&uacute; (BCP) <br/>
// *         <u>Service Provider</u>: NTTData <br/>
// *         <u>Developed by</u>: <br/>
// *         <ul>
// *         <li>Juan Carlos Hilario Ram?rez</li>
// *         </ul>
// *         <u>Changes</u>:<br/>
// *         <ul>
// *         <li>Jan 11, 2023 Creaci&oacute;n de Clase.</li>
// *         </ul>
// * @version 1.0
// */
//@Component
//@Mapper(componentModel = "spring",
//    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
//    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
//
//public interface ProductMapper {
//
//
//  /**
//   * Domain product to product response.
//   *
//   * @param dto the dto
//   * @return the device get response
//   */
//  @Mappings({
////      @Mapping(target = "registerDate", dateFormat = DatePatternConstant.DEVICE_DATE_PATTERN),
////      @Mapping(target = "lastLoginDate", dateFormat = DatePatternConstant.DEVICE_DATE_PATTERN),
//      @Mapping(source = "id", target = "id"),})
//  ProductResponse domainDeviceToFindResponse(ProductDto dto);
//
//
//
//  List<ProductResponse> domainDeviceToRemoveDevice(List<ProductDto> processedDevices);
//
//}
