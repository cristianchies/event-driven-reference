package com.github.rodrigorfk.basket.service;

import com.github.rodrigorfk.store.commons.data.StorePreferenceData;
import com.github.rodrigorfk.store.commons.data.StorePreferenceSearchRequestData;
import com.github.rodrigorfk.store.commons.data.StorePreferenceSearchResponseData;
import com.github.rodrigorfk.store.message.StorePreferenceInputQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Service
public class StoreService {

    private final Queue<TickerCompletableFuture> requestStorePreference = new ConcurrentLinkedQueue<TickerCompletableFuture>();

    @Autowired
    private StorePreferenceInputQueue queue;

    public Future<StorePreferenceData> getStorePreference(String ticker, Long storeId){
        StorePreferenceSearchRequestData request = StorePreferenceSearchRequestData
                .builder()
                .ticker(ticker).storeId(storeId)
                .build();

        TickerCompletableFuture<StorePreferenceData, Long> future = new TickerCompletableFuture<>(ticker, storeId);
        this.requestStorePreference.add(future);
        queue.requestOutput().send(MessageBuilder.withPayload(request).build());
        return future;
    }

    @StreamListener(StorePreferenceInputQueue.STORE_PREFERENCE_RESPONSE_INPUT_QUEUE)
    public void storePreferenceResponse(StorePreferenceSearchResponseData response){
        for(TickerCompletableFuture<StorePreferenceData, Long> future:this.requestStorePreference){
            if(future.getTicker().equals(response.getTicker()) || (response.getStorePreference() != null && future.getAdditionalData().equals(response.getStorePreference().getStoreId()))){
                future.complete(response.getStorePreference());
                this.requestStorePreference.remove(future);
            }
        }
    }
}
