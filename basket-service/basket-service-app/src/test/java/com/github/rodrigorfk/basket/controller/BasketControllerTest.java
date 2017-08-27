package com.github.rodrigorfk.basket.controller;

import com.github.rodrigorfk.basket.controller.data.AddProductData;
import com.github.rodrigorfk.basket.data.BasketEntity;
import com.github.rodrigorfk.basket.messaging.BasketAddProductEvent;
import com.github.rodrigorfk.basket.messaging.BasketAddProductResponse;
import com.github.rodrigorfk.basket.messaging.BasketProductQueue;
import com.github.rodrigorfk.basket.repository.BasketEntityRepository;
import com.github.rodrigorfk.product.commons.data.ProductStoreData;
import com.github.rodrigorfk.product.commons.data.ProductStoreRequestData;
import com.github.rodrigorfk.product.messaging.ProductStoreInputQueue;
import com.github.rodrigorfk.store.commons.data.StorePreferenceData;
import com.github.rodrigorfk.store.commons.data.StorePreferenceSearchRequestData;
import com.github.rodrigorfk.store.commons.data.StorePreferenceSearchResponseData;
import com.github.rodrigorfk.store.message.StorePreferenceInputQueue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasketControllerTest {

    private MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    private BasketEntityRepository basketEntityRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MessageCollector collector;
    @Autowired
    private StorePreferenceInputQueue queueStore;
    @Autowired
    private ProductStoreInputQueue queueProduct;
    @Autowired
    private BasketProductQueue queueBasket;
    @Autowired
    private Executor executor;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void createBasket() throws Exception {

        MvcResult result = this.mockMvc.perform(get("/store/1/customer/10/basket").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("storeId", is(1)))
                .andExpect(jsonPath("customerId", is(10)))
                .andExpect(jsonPath("total", is(0.0)))
                .andReturn();

        BasketEntity basket = mapper.readValue(result.getResponse().getContentAsString(), BasketEntity.class);
        basket = basketEntityRepository.findOne(basket.getId());
        basket.setTotal(BigDecimal.ONE.doubleValue());
        basketEntityRepository.save(basket);

        this.mockMvc.perform(get("/store/1/customer/10/basket").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(basket.getId())))
                .andExpect(jsonPath("storeId", is(1)))
                .andExpect(jsonPath("customerId", is(10)))
                .andExpect(jsonPath("total", is(1.0)));
    }

    @Test
    public void addProductBasketNotFound() throws Exception {
        BlockingQueue<Message<?>> channelRequestBasketAddProduct = collector.forChannel(queueBasket.requestOutput());
        channelRequestBasketAddProduct.clear();
        BlockingQueue<Message<?>> channelResponseBasketAddProduct = collector.forChannel(queueBasket.responseOutput());
        channelResponseBasketAddProduct.clear();

        AddProductData data = AddProductData.builder()
                .productId(34343l)
                .quantity(BigDecimal.ONE)
                .build();

        MvcResult mvcResult = this.mockMvc.perform(
                post("/basket/43fdsf5tfsafdasd/products")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsBytes(data))
        )
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        Message<?> requestMessage = channelRequestBasketAddProduct.take();
        BasketAddProductEvent event = (BasketAddProductEvent) requestMessage.getPayload();
        Assert.assertEquals("43fdsf5tfsafdasd", event.getBasketId());
        Assert.assertEquals(data, event.getData());
        executor.execute(() -> {
            queueBasket.requestInput().send(MessageBuilder.withPayload(event).build());
        });

        requestMessage = channelResponseBasketAddProduct.take();
        BasketAddProductResponse responseEntity = (BasketAddProductResponse) requestMessage.getPayload();
        String ticker = requestMessage.getHeaders().get("ticker").toString();

        executor.execute(() -> {
            queueBasket.responseInput().send(MessageBuilder.withPayload(responseEntity).setHeader("ticker", ticker).build());
        });

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addProductSuccess() throws Exception {

        BlockingQueue<Message<?>> channelStorePreference = collector.forChannel(queueStore.requestOutput());
        channelStorePreference.clear();
        BlockingQueue<Message<?>> channelRequestBasketAddProduct = collector.forChannel(queueBasket.requestOutput());
        channelRequestBasketAddProduct.clear();
        BlockingQueue<Message<?>> channelResponseBasketAddProduct = collector.forChannel(queueBasket.responseOutput());
        channelResponseBasketAddProduct.clear();
        BlockingQueue<Message<?>> channelProductPreference = collector.forChannel(queueProduct.requestOutput());
        channelProductPreference.clear();

        MvcResult result = this.mockMvc.perform(get("/store/1/customer/11/basket").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("storeId", is(1)))
                .andExpect(jsonPath("customerId", is(11)))
                .andExpect(jsonPath("total", is(0.0)))
                .andReturn();

        BasketEntity basket = mapper.readValue(result.getResponse().getContentAsString(), BasketEntity.class);

        AddProductData data = AddProductData.builder()
                .productId(34343L)
                .quantity(BigDecimal.ONE)
                .build();

        MvcResult mvcResult = this.mockMvc.perform(
                post("/basket/{basketId}/products", basket.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(mapper.writeValueAsBytes(data))
        )
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        Message<?> requestMessage = channelRequestBasketAddProduct.take();
        BasketAddProductEvent event = (BasketAddProductEvent) requestMessage.getPayload();
        Assert.assertEquals(basket.getId(), event.getBasketId());
        Assert.assertEquals(data, event.getData());
        executor.execute(() -> {
            queueBasket.requestInput().send(MessageBuilder.withPayload(event).build());
        });

        requestMessage = channelStorePreference.take();
        StorePreferenceSearchRequestData request = (StorePreferenceSearchRequestData) requestMessage.getPayload();
        Assert.assertEquals(1L, request.getStoreId().longValue());

        StorePreferenceSearchResponseData response = StorePreferenceSearchResponseData.builder()
                .ticker(request.getTicker())
                .storePreference(
                        StorePreferenceData.builder()
                        .storeId(request.getStoreId())
                        .maxItensInCart(10)
                        .build()
                ).build();
        queueStore.responseInput().send(MessageBuilder.withPayload(response).build());

        requestMessage = channelProductPreference.take();
        ProductStoreRequestData requestProduct = (ProductStoreRequestData) requestMessage.getPayload();
        Assert.assertEquals(1L, requestProduct.getStoreId().longValue());
        Assert.assertEquals(34343L, requestProduct.getProductId().longValue());

        ProductStoreData productStoreData = ProductStoreData.builder()
                .id(34343L).storeId(1L).stock(20.0).price(10.0).build();
        queueProduct.responseInput().send(MessageBuilder.withPayload(productStoreData).setHeader("ticker", request.getTicker()).build());

        requestMessage = channelResponseBasketAddProduct.take();
        BasketAddProductResponse responseEntity = (BasketAddProductResponse) requestMessage.getPayload();
        Assert.assertEquals(request.getTicker(), requestMessage.getHeaders().get("ticker"));

        executor.execute(() -> {
            queueBasket.responseInput().send(MessageBuilder.withPayload(responseEntity).setHeader("ticker", request.getTicker()).build());
        });

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("storeId", is(1)))
                .andExpect(jsonPath("customerId", is(11)))
                .andExpect(jsonPath("subTotal", is(10.0)))
                .andExpect(jsonPath("total", is(10.0)))
                .andExpect(jsonPath("products[0].productId", is(data.getProductId().intValue())))
                .andExpect(jsonPath("products[0].quantity", is(data.getQuantity().doubleValue())))
                .andExpect(jsonPath("products[0].unitPrice", is(10.0)))
                .andExpect(jsonPath("products[0].total", is(10.0)));
    }
}