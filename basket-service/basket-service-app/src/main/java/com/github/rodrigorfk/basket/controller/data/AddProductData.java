package com.github.rodrigorfk.basket.controller.data;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Data
@Builder
public class AddProductData {

    private Long productId;
    private BigDecimal quantity;
}
