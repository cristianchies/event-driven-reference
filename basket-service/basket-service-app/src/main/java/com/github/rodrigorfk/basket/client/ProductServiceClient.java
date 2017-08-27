package com.github.rodrigorfk.basket.client;

import com.github.rodrigorfk.product.commons.data.ProductStoreData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@FeignClient(name = "product-service", url = "${service.product.url}", decode404 = true)
public interface ProductServiceClient {

    @RequestMapping(method = RequestMethod.GET, path = "/product/{id}/store/{storeId}")
    public ResponseEntity<ProductStoreData> getProductStore(@PathVariable("storeId") Long storeId, @PathVariable("id") Long productId);
}
