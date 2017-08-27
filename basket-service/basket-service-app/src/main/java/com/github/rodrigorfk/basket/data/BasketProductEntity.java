package com.github.rodrigorfk.basket.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketProductEntity {

    private Long productId;
    private Double quantity;
    private Double unitPrice;
    private Double total;

    private List<Long> rules = new ArrayList<>();
}
