package com.github.rodrigorfk.product.service;

import com.github.rodrigorfk.product.commons.data.ProductStoreData;
import com.github.rodrigorfk.product.commons.data.ProductStoreRequestData;
import com.github.rodrigorfk.product.data.ProductEntity;
import com.github.rodrigorfk.product.messaging.ProductStoreOutputQueue;
import com.github.rodrigorfk.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductStoreOutputQueue queue;

    @StreamListener(ProductStoreOutputQueue.PRODUCT_STORE_REQUEST_INPUT_QUEUE)
    public void productStoreRequest(ProductStoreRequestData request){
        ProductStoreData data = this.getProductStore(request);
        if(data == null){
            data = new ProductStoreData();
        }
        queue.responseOutput().send(
                MessageBuilder.withPayload(data).setHeader("ticker", request.getTicker()).build()
        );
    }

    public ProductStoreData getProductStore(ProductStoreRequestData request){
        ProductEntity entity = repository.findOne(request.getProductId());

        if(entity != null) {
            ProductStoreData.ProductStoreDataBuilder dataBuilder = ProductStoreData.builder()
                    .id(entity.getId())
                    .description(entity.getDescription())
                    .unitPrice(entity.getUnitPrice());

            dataBuilder.storeId(request.getStoreId());
            ProductEntity.ProductStoreEntity productStore = entity.getStores().get(request.getStoreId().toString());
            if(productStore != null){
                dataBuilder.stock(productStore.getStock());
                dataBuilder.price(productStore.getPrice());
            }

            return dataBuilder.build();
        }
        return null;
    }
}
