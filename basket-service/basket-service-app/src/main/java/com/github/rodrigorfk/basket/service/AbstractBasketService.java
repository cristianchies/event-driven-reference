package com.github.rodrigorfk.basket.service;

import com.github.rodrigorfk.basket.controller.data.AddProductData;
import com.github.rodrigorfk.basket.data.BasketEntity;
import com.github.rodrigorfk.basket.data.BasketProductEntity;
import com.github.rodrigorfk.basket.repository.BasketEntityRepository;
import com.github.rodrigorfk.product.commons.data.ProductStoreData;
import com.github.rodrigorfk.store.commons.data.StorePreferenceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public abstract class AbstractBasketService {

    @Autowired
    protected BasketEntityRepository basketEntityRepository;
    @Autowired
    protected MongoTemplate mongoTemplate;

    protected BasketEntity insertProductOnBasket(AddProductData productRequest, ProductStoreData productStore, BasketEntity basket) {
        BigDecimal unitPrice = BigDecimal.valueOf(productStore.getPrice());
        BasketProductEntity productOnBasket = BasketProductEntity.builder()
                .productId(productRequest.getProductId())
                .quantity(productRequest.getQuantity().doubleValue())
                .unitPrice(unitPrice.doubleValue())
                .total(productRequest.getQuantity().multiply(unitPrice).doubleValue())
                .build();
        Query query = new Query(Criteria.where("_id").is(basket.getId()));
        Update update = new Update()
                .push("products", productOnBasket)
                .inc("subTotal", productOnBasket.getTotal().doubleValue())
                .inc("total", productOnBasket.getTotal().doubleValue());
        return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), BasketEntity.class);
    }

    protected BasketEntity updateProductOnBasket(AddProductData productRequest, ProductStoreData productStore, BasketEntity basket, BasketProductEntity productOnBasket) {
        BigDecimal unitPrice = BigDecimal.valueOf(productStore.getPrice());
        int index = basket.getProducts().indexOf(productOnBasket);
        String productsKey = String.format("products.%s.", Integer.toString(index));
        Query query = new Query(Criteria.where("_id").is(basket.getId()));
        double value = productRequest.getQuantity().multiply(unitPrice).doubleValue();
        Update update = new Update()
                .inc(productsKey+"quantity", productRequest.getQuantity().doubleValue())
                .inc(productsKey+"total", value)
                .inc("subTotal", value)
                .inc("total", value);
        return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), BasketEntity.class);
    }

    protected ResponseEntity validateStorePreference(BasketEntity basket, StorePreferenceData storePreference, boolean addProduct) {

        if(storePreference != null){
            if(addProduct && basket.getProducts().size() >= storePreference.getMaxItensInCart()){
                return ResponseEntity.badRequest().build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
        return null;
    }

    protected ResponseEntity validateProductStore(ProductStoreData productStore, AddProductData productRequest, BasketProductEntity productOnBasket) {
        if(productStore != null && productStore.getId() != null && productStore.getStock() != null){
            BigDecimal quantity = productRequest.getQuantity().add(productOnBasket != null ? BigDecimal.valueOf(productOnBasket.getQuantity()) : BigDecimal.ZERO);
            if(quantity.doubleValue() <= productStore.getStock()){
                return null;
            }
        }
        return ResponseEntity.notFound().build();
    }
}
