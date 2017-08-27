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
public class ProductStoreData {

    private Long id;
    private String description;
    private String unitPrice;
    private Long storeId;
    private Double stock;
    private Double price;
}
