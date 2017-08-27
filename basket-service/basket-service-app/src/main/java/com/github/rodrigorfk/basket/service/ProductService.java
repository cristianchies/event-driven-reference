package com.github.rodrigorfk.basket.service;

import com.github.rodrigorfk.product.commons.data.ProductStoreData;
import com.github.rodrigorfk.product.commons.data.ProductStoreRequestData;
import com.github.rodrigorfk.product.messaging.ProductStoreInputQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Service
public class ProductService {

    private final Queue<TickerCompletableFuture<ProductStoreData, ProductStoreRequestData>> requestStorePreference = new ConcurrentLinkedQueue<>();

    @Autowired
    private ProductStoreInputQueue queue;

    public Future<ProductStoreData> getProductStore(String ticker, Long storeId, Long productId){
        ProductStoreRequestData request = ProductStoreRequestData
                .builder()
                .ticker(ticker).storeId(storeId).productId(productId)
                .build();

        TickerCompletableFuture<ProductStoreData, ProductStoreRequestData> future = new TickerCompletableFuture<>(ticker, request);
        this.requestStorePreference.add(future);
        queue.requestOutput().send(MessageBuilder.withPayload(request).build());
        return future;
    }

    @StreamListener(ProductStoreInputQueue.PRODUCT_STORE_RESPONSE_INPUT_QUEUE)
    public void productStoreResponse(@Payload ProductStoreData response, @Header String ticker){
        for(TickerCompletableFuture<ProductStoreData, ProductStoreRequestData> future:this.requestStorePreference){
            if(compareData(response, ticker, future)){
                future.complete(response);
                this.requestStorePreference.remove(future);
            }
        }
    }

    private boolean compareData(ProductStoreData response, String ticker, TickerCompletableFuture<ProductStoreData, ProductStoreRequestData> future) {
        return future.getTicker().equals(ticker) ||
                (response.getId() != null
                        && future.getAdditionalData().getProductId().equals(response.getId())
                        && future.getAdditionalData().getStoreId().equals(response.getStoreId())
                );
    }


}
