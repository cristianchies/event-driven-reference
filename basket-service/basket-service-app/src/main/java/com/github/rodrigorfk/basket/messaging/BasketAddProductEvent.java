package com.github.rodrigorfk.basket.messaging;

import com.github.rodrigorfk.basket.controller.data.AddProductData;
import lombok.Builder;
import lombok.Data;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Data
@Builder
public class BasketAddProductEvent {

    private String ticker;
    private String basketId;
    private AddProductData data;
}
