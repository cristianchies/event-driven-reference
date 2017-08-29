package org.github.rodrigorfk.customer.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public interface CustomerEventQueue {

//    @Input("customer-event-input")
//    SubscribableChannel input();

    @Output("customer-event-output")
    MessageChannel output();
}
