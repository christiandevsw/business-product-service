package com.dojo.food.services.business.menu.expose;

//import com.dojo.food.services.business.menu.expose.mapper.ProductMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class DemoController {
//    ProductService productService;


//    @GetMapping("/products")
//    Maybe<ResponseEntity<Flowable<ProductResponse>>> listProducts(ProductQuery query, ProductHeader header, ServerWebExchange exchange) {
//
//        return Maybe.fromCallable(() -> productService.findProducts().map(productDto -> {
//
//            ProductResponse response = new ProductResponse();
//            response.setId(productDto.getId());
//            response.setAvailable(productDto.getAvailable());
//
//            return response;
//        })).map(response -> ResponseEntity.ok(response)).subscribeOn(Schedulers.io()).doOnSubscribe(disposable -> log.info("Endpoint GET By CIC - Starting")).doOnSuccess(response -> log.info("Endpoint GET By CIC - Completed"));
//    }

    @GetMapping("message")
    public String showMessage() {
        return "hello world";
    }

}
