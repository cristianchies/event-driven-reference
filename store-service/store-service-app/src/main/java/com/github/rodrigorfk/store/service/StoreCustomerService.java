package com.github.rodrigorfk.store.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Service
@Slf4j
public class StoreCustomerService {

    @StreamListener("customer-event-input")
    public void onCustomerCreated(Map event) throws Exception {
        log.info("customerCreated: {}", event.get("id"));
        String name = (String) event.get("name");
        if(name != null && name.toLowerCase().indexOf("teste") != -1){
            throw new Exception("aqui não");
        }
    }
}
