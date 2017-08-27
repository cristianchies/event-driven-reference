package com.github.rodrigorfk.product.service;

import com.github.rodrigorfk.product.commons.data.ProductStoreData;
import com.github.rodrigorfk.product.commons.data.ProductStoreRequestData;
import com.github.rodrigorfk.product.data.ProductEntity;
import com.github.rodrigorfk.product.messaging.ProductStoreOutputQueue;
import com.github.rodrigorfk.product.repository.ProductRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductServiceTest {

    @Autowired
    private MessageCollector collector;
    @Autowired
    private ProductService service;
    @Autowired
    private ProductStoreOutputQueue queue;
    @Autowired
    private ProductRepository repository;

    @Test
    public void testGetProductData(){
        ProductStoreRequestData request = ProductStoreRequestData.builder()
                .ticker(UUID.randomUUID().toString())
                .productId(1L).storeId(1L).build();

        ProductStoreData data = service.getProductStore(request);
        Assert.assertNull(data);
    }

    @Test
    public void testproductStoreRequest() throws InterruptedException {
        BlockingQueue<Message<?>> channel = collector.forChannel(queue.responseOutput());
        channel.clear();

        ProductStoreRequestData request = ProductStoreRequestData.builder()
                .ticker(UUID.randomUUID().toString())
                .productId(1L).storeId(1L).build();

        service.productStoreRequest(request);

        Message<?> message = channel.take();
        ProductStoreData data = (ProductStoreData) message.getPayload();
        Assert.assertNull(data.getId());
        Assert.assertEquals(request.getTicker(), message.getHeaders().get("ticker").toString());

        ProductEntity entity = new ProductEntity();
        entity.setId(2L);
        repository.save(entity);

        entity = new ProductEntity();
        entity.setId(3L);
        entity.setDescription("Product 3");
        entity.setUnitPrice("UN");
        ProductEntity.ProductStoreEntity productStoreEntity = new ProductEntity.ProductStoreEntity();
        productStoreEntity.setPrice(10.0);
        productStoreEntity.setStock(8.0);
        entity.getStores().put("1", productStoreEntity);
        repository.save(entity);

        request = ProductStoreRequestData.builder()
                .ticker(UUID.randomUUID().toString())
                .productId(2L).storeId(1L).build();

        service.productStoreRequest(request);

        message = channel.take();
        data = (ProductStoreData) message.getPayload();
        Assert.assertEquals(2L, data.getId().longValue());
        Assert.assertNull(data.getPrice());
        Assert.assertEquals(request.getTicker(), message.getHeaders().get("ticker").toString());

        request = ProductStoreRequestData.builder()
                .ticker(UUID.randomUUID().toString())
                .productId(3L).storeId(1L).build();

        service.productStoreRequest(request);

        message = channel.take();
        data = (ProductStoreData) message.getPayload();
        Assert.assertEquals(3L, data.getId().longValue());
        Assert.assertEquals("Product 3", data.getDescription());
        Assert.assertEquals("UN", data.getUnitPrice());
        Assert.assertEquals(1L, data.getStoreId().longValue());
        Assert.assertEquals(10.0, data.getPrice().doubleValue(), 0);
        Assert.assertEquals(8.0, data.getStock().doubleValue(), 0);
        Assert.assertEquals(request.getTicker(), message.getHeaders().get("ticker").toString());
    }
}