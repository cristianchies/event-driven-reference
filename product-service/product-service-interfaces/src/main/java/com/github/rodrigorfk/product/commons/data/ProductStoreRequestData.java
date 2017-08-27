package com.github.rodrigorfk.product.commons.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStoreRequestData {

    private String ticker;
    private Long productId;
    private Long storeId;
}
