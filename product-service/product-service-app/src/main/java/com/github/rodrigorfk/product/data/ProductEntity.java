package com.github.rodrigorfk.product.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Data
public class ProductEntity {

    @Id
    private Long id;
    private String description;
    private String unitPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @CreatedDate
    private LocalDateTime createdDate = LocalDateTime.now();
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private Map<String,ProductStoreEntity> stores = new HashMap<>();

    @Data
    public static class ProductStoreEntity {

        private Double stock;
        private Double price;
    }
}
