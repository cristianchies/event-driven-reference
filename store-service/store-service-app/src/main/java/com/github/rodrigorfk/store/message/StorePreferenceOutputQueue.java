package com.github.rodrigorfk.store.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public interface StorePreferenceOutputQueue {

    public static final String STORE_PREFERENCE_RESPONSE_OUTPUT_QUEUE = "store-preference-response-output";
    public static final String STORE_PREFERENCE_REQUEST_INPUT_QUEUE = "store-preference-request-input";

    @Output(STORE_PREFERENCE_RESPONSE_OUTPUT_QUEUE)
    MessageChannel responseOutput();

    @Input(STORE_PREFERENCE_REQUEST_INPUT_QUEUE)
    SubscribableChannel requestInput();

}
