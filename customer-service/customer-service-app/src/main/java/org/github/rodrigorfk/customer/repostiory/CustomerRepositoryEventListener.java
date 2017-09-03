package org.github.rodrigorfk.customer.repostiory;

import org.github.rodrigorfk.customer.data.CustomerEntity;
import org.github.rodrigorfk.customer.messaging.CustomerEventQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Component
public class CustomerRepositoryEventListener extends AbstractRepositoryEventListener<CustomerEntity>{

    @Autowired
    private CustomerEventQueue queue;

    @Override
    protected void onBeforeCreate(CustomerEntity entity) {

        queue.output().send(
                MessageBuilder.withPayload(entity)
                        .setHeader("action", "created")
                        .setHeader("customerId", entity.getId())
                        .build()
        );

    }
}
