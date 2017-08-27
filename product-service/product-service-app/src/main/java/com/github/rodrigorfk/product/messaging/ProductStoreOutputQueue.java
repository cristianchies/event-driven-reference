package com.github.rodrigorfk.product.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public interface ProductStoreOutputQueue {

    public static final String PRODUCT_STORE_RESPONSE_OUTPUT_QUEUE = "product-store-response-output";
    public static final String PRODUCT_STORE_REQUEST_INPUT_QUEUE = "product-store-request-input";

    @Output(PRODUCT_STORE_RESPONSE_OUTPUT_QUEUE)
    MessageChannel responseOutput();

    @Input(PRODUCT_STORE_REQUEST_INPUT_QUEUE)
    SubscribableChannel requestInput();

}
