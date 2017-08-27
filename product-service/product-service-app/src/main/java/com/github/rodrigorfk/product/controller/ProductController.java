package com.github.rodrigorfk.product.controller;

import com.github.rodrigorfk.product.commons.data.ProductStoreData;
import com.github.rodrigorfk.product.commons.data.ProductStoreRequestData;
import com.github.rodrigorfk.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@RestController
public class ProductController {

    @Autowired
    private ProductService service;

    @RequestMapping("/product/{id}/store/{storeId}")
    public ResponseEntity<ProductStoreData> getProductStore(@PathVariable("storeId") Long storeId, @PathVariable("id") Long productId) {
        ProductStoreData productStore = service.getProductStore(ProductStoreRequestData.builder()
                .storeId(storeId).productId(productId).build());

        if(productStore == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productStore);
    }
}
