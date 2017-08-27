package com.github.rodrigorfk.product.controller;

import com.github.rodrigorfk.product.data.ProductEntity;
import com.github.rodrigorfk.product.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {

    @Autowired
    private ProductRepository repository;
    private MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void testProductNotFound() throws Exception {

        this.mockMvc.perform(get("/product/{id}/store/{storeId}", 1L, 1L).accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testProduct() throws Exception {

        ProductEntity entity = new ProductEntity();
        entity.setId(4L);
        entity.setDescription("Product 4");
        entity.setUnitPrice("UN");
        ProductEntity.ProductStoreEntity productStoreEntity = new ProductEntity.ProductStoreEntity();
        productStoreEntity.setPrice(10.0);
        productStoreEntity.setStock(8.0);
        entity.getStores().put(1L, productStoreEntity);
        repository.save(entity);

        this.mockMvc.perform(get("/product/{id}/store/{storeId}", 4L, 1L).accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(4)))
                .andExpect(jsonPath("description", is("Product 4")))
                .andExpect(jsonPath("unitPrice", is("UN")))
                .andExpect(jsonPath("storeId", is(1)))
                .andExpect(jsonPath("price", is(10.0)))
                .andExpect(jsonPath("stock", is(8.0)));
    }


}