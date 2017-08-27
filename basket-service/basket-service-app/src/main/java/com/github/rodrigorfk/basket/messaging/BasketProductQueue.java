package com.github.rodrigorfk.basket.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public interface BasketProductQueue {

    public static final String BASKET_ADD_PRODUCT_REQUEST_INPUT = "basket-addproduct-request-input";
    public static final String BASKET_ADD_PRODUCT_REQUEST_OUTPUT = "basket-addproduct-request-output";
    public static final String BASKET_ADD_PRODUCT_RESPONSE_INPUT = "basket-addproduct-response-input";
    public static final String BASKET_ADD_PRODUCT_RESPONSE_OUTPUT = "basket-addproduct-response-output";

    @Input(BASKET_ADD_PRODUCT_REQUEST_INPUT)
    SubscribableChannel requestInput();
    @Output(BASKET_ADD_PRODUCT_REQUEST_OUTPUT)
    MessageChannel requestOutput();

    @Input(BASKET_ADD_PRODUCT_RESPONSE_INPUT)
    SubscribableChannel responseInput();
    @Output(BASKET_ADD_PRODUCT_RESPONSE_OUTPUT)
    MessageChannel responseOutput();
}
