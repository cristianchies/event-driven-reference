package com.github.rodrigorfk.store.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public interface CustomerEventQueue {

    @Input("customer-event-input")
    SubscribableChannel input();
}
