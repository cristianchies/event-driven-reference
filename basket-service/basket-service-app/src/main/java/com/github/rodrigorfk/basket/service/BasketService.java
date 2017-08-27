package com.github.rodrigorfk.basket.service;

import com.github.rodrigorfk.basket.controller.BasketDeferredResult;
import com.github.rodrigorfk.basket.controller.data.AddProductData;
import com.github.rodrigorfk.basket.data.BasketEntity;
import com.github.rodrigorfk.basket.data.BasketProductEntity;
import com.github.rodrigorfk.basket.data.BasketStatus;
import com.github.rodrigorfk.basket.messaging.BasketAddProductEvent;
import com.github.rodrigorfk.basket.messaging.BasketAddProductResponse;
import com.github.rodrigorfk.basket.messaging.BasketProductQueue;
import com.github.rodrigorfk.product.commons.data.ProductStoreData;
import com.github.rodrigorfk.store.commons.data.StorePreferenceData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Service
@Slf4j
public class BasketService extends AbstractBasketService{

    private final Queue<BasketDeferredResult> requestAddProduct = new ConcurrentLinkedQueue<BasketDeferredResult>();

    @Autowired
    private BasketProductQueue queue;
    @Autowired
    protected StoreService storeService;
    @Autowired
    protected ProductService productService;

    public BasketEntity getDetails(Long storeId, Long customerId){
        Query query = new Query(
                Criteria.where("customerId").is(customerId)
                        .and("storeId").is(storeId)
                        .and("status").is(BasketStatus.PENDING)
        );
        Update update = new Update();
        return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().upsert(true).returnNew(true), BasketEntity.class);
    }

    public void addProduct(String basketId, AddProductData productRequest, BasketDeferredResult deferredResult) {
        requestAddProduct.add(deferredResult);
        BasketAddProductEvent event = BasketAddProductEvent.builder()
                .ticker(deferredResult.getTicker())
                .basketId(basketId)
                .data(productRequest)
                .build();
        this.queue.requestOutput().send(MessageBuilder.withPayload(event).build());
    }

    @StreamListener(BasketProductQueue.BASKET_ADD_PRODUCT_REQUEST_INPUT)
    public void addProductListener(BasketAddProductEvent event) {
        String basketId = event.getBasketId();
        AddProductData productRequest = event.getData();
        ResponseEntity result = null;
        BasketEntity basket = basketEntityRepository.findOne(basketId);
        if(basket != null){
            BasketProductEntity productOnBasket = basket.getProducts().stream()
                    .filter(t -> t.getProductId().equals(productRequest.getProductId()))
                    .findFirst().orElse(null);

            Future<StorePreferenceData> storePreferenceDataFuture = storeService
                    .getStorePreference(event.getTicker(), basket.getStoreId());

            Future<ProductStoreData> productStoreFuture = productService
                    .getProductStore(event.getTicker(), basket.getStoreId(), productRequest.getProductId());

            try {
                result = validateStorePreference(basket, storePreferenceDataFuture.get(), productOnBasket != null);
                if(result == null){
                    ProductStoreData productStore = productStoreFuture.get();
                    result = validateProductStore(productStore, productRequest, productOnBasket);
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

            } catch (InterruptedException | ExecutionException e) {
                log.error("Error waiting for task", e);
                result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } else {
            result = ResponseEntity.notFound().build();
        }
        BasketAddProductResponse response = new BasketAddProductResponse(result.getStatusCode(), (BasketEntity) result.getBody());
        queue.responseOutput().send(MessageBuilder.withPayload(response).setHeader("ticker", event.getTicker()).build());
    }

    @StreamListener(BasketProductQueue.BASKET_ADD_PRODUCT_RESPONSE_INPUT)
    public void addProductResponseListener(@Payload BasketAddProductResponse response, @Header("ticker") String ticker){
        for(BasketDeferredResult future:this.requestAddProduct){
            if(future.getTicker().equals(ticker)){
                future.setResult(ResponseEntity.status(response.getStatusCode()).body(response.getEntity()));
                this.requestAddProduct.remove(future);
            }
        }
    }

}
