package com.github.rodrigorfk.store.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public interface StorePreferenceInputQueue {

    public static final String STORE_PREFERENCE_RESPONSE_INPUT_QUEUE = "store-preference-response-input";
    public static final String STORE_PREFERENCE_REQUEST_OUTPUT_QUEUE = "store-preference-request-output";

    @Input(STORE_PREFERENCE_RESPONSE_INPUT_QUEUE)
    SubscribableChannel responseInput();

    @Output(STORE_PREFERENCE_REQUEST_OUTPUT_QUEUE)
    MessageChannel requestOutput();
}
