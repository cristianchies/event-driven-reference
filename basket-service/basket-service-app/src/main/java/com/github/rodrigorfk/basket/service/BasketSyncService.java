package com.github.rodrigorfk.basket.service;

import com.github.rodrigorfk.basket.client.ProductServiceClient;
import com.github.rodrigorfk.basket.client.StoreServiceClient;
import com.github.rodrigorfk.basket.controller.data.AddProductData;
import com.github.rodrigorfk.basket.data.BasketEntity;
import com.github.rodrigorfk.basket.data.BasketProductEntity;
import com.github.rodrigorfk.product.commons.data.ProductStoreData;
import com.github.rodrigorfk.store.commons.data.StorePreferenceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Service
public class BasketSyncService extends AbstractBasketService{

    @Autowired
    private StoreServiceClient storeServiceClient;
    @Autowired
    private ProductServiceClient productServiceClient;

    public ResponseEntity<BasketEntity> addProduct(String basketId, AddProductData productRequest) {
        BasketEntity basket = basketEntityRepository.findOne(basketId);
        ResponseEntity result = ResponseEntity.notFound().build();
        if(basket != null) {
            BasketProductEntity productOnBasket = basket.getProducts().stream()
                    .filter(t -> t.getProductId().equals(productRequest.getProductId()))
                    .findFirst().orElse(null);

            Resource<StorePreferenceData> storePreference = storeServiceClient.getStorePreference(basket.getStoreId());

            result = this.validateStorePreference(basket, storePreference.getContent(), productOnBasket != null);
            if(result == null){
                ResponseEntity<ProductStoreData> productStoreEntity = productServiceClient.getProductStore(basket.getStoreId(), productRequest.getProductId());
                ProductStoreData productStore = productStoreEntity.getBody();
                result = this.validateProductStore(productStore, productRequest, productOnBasket);
                if(result == null) {
                    if (productOnBasket == null) {
                        basket = insertProductOnBasket(productRequest, productStore, basket);
                        result = ResponseEntity.ok(basket);
                    } else {
                        basket = updateProductOnBasket(productRequest, productStore, basket, productOnBasket);
                        result = ResponseEntity.ok(basket);
                    }
                }
            }
        }
        return result;
    }
}
