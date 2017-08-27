package com.github.rodrigorfk.product.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public interface ProductStoreInputQueue {

    public static final String PRODUCT_STORE_RESPONSE_INPUT_QUEUE = "product-store-response-input";
    public static final String PRODUCT_STORE_REQUEST_OUTPUT_QUEUE = "product-store-request-output";

    @Input(PRODUCT_STORE_RESPONSE_INPUT_QUEUE)
    SubscribableChannel responseInput();

    @Output(PRODUCT_STORE_REQUEST_OUTPUT_QUEUE)
    MessageChannel requestOutput();
}
