package com.github.rodrigorfk.basket;

import com.github.rodrigorfk.basket.messaging.BasketProductQueue;
import com.github.rodrigorfk.product.messaging.ProductStoreInputQueue;
import com.github.rodrigorfk.store.message.StorePreferenceInputQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@SpringBootApplication
@EnableBinding({StorePreferenceInputQueue.class, BasketProductQueue.class, ProductStoreInputQueue.class})
@EnableAsync
@EnableFeignClients
public class BasketApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasketApplication.class, args);
    }
}
