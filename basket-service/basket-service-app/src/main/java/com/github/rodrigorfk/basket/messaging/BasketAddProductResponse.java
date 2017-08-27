package com.github.rodrigorfk.basket.messaging;

import com.github.rodrigorfk.basket.data.BasketEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Data
@NoArgsConstructor
public class BasketAddProductResponse {

    private HttpStatus statusCode;
    private BasketEntity entity;

    public BasketAddProductResponse(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public BasketAddProductResponse(HttpStatus statusCode, BasketEntity entity) {
        this.statusCode = statusCode;
        this.entity = entity;
    }
}
