package com.github.rodrigorfk.basket.controller;

import com.github.rodrigorfk.basket.controller.data.AddProductData;
import com.github.rodrigorfk.basket.data.BasketEntity;
import com.github.rodrigorfk.basket.service.BasketService;
import com.github.rodrigorfk.basket.service.BasketSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@RestController
public class BasketController {

    @Autowired
    private BasketService basketService;
    @Autowired
    private BasketSyncService basketSyncService;

    @RequestMapping(method = RequestMethod.GET, path = "/store/{storeId}/customer/{customerId}/basket")
    public BasketEntity getDetails(@PathVariable("storeId") Long storeId, @PathVariable("customerId") Long customerId){
        BasketEntity basket = basketService.getDetails(storeId, customerId);
        return basket;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/basket/{basketId}/products")
    public BasketDeferredResult addProduct(@PathVariable("basketId") String basketId, @RequestBody @Valid AddProductData productRequest){
        String ticker = UUID.randomUUID().toString();
        BasketDeferredResult deferredResult = new BasketDeferredResult(ticker);

        basketService.addProduct(basketId, productRequest, deferredResult);
        return deferredResult;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/basket/{basketId}/productsSync")
    public ResponseEntity<BasketEntity> addProductSync(@PathVariable("basketId") String basketId, @RequestBody @Valid AddProductData productRequest){
        return basketSyncService.addProduct(basketId, productRequest);
    }
}
