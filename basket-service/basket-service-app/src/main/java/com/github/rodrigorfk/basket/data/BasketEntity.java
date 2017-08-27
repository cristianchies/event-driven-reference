package com.github.rodrigorfk.basket.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Data
@Document(collection = "basket")
public class BasketEntity {

    @Id
    private String id;

    private Long storeId;
    private Long customerId;
    private BasketStatus status = BasketStatus.PENDING;

    private Double subTotal = BigDecimal.ZERO.doubleValue();
    private Double freightPrice = BigDecimal.ZERO.doubleValue();
    private Double total = BigDecimal.ZERO.doubleValue();

    @Version
    private Long version;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @CreatedDate
    private LocalDateTime createdDate = LocalDateTime.now();
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private List<Long> rules = new ArrayList<>();
    private List<BasketProductEntity> products = new ArrayList<>();
}
