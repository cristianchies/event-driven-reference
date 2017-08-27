package com.github.rodrigorfk.store.service;

import com.github.rodrigorfk.store.commons.data.StorePreferenceSearchRequestData;
import com.github.rodrigorfk.store.commons.data.StorePreferenceSearchResponseData;
import com.github.rodrigorfk.store.data.StorePreferenceEntity;
import com.github.rodrigorfk.store.message.StorePreferenceOutputQueue;
import com.github.rodrigorfk.store.repository.StorePreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Service
public class StorePreferenceService {

    @Autowired
    private StorePreferenceRepository storePreferenceRepository;
    @Autowired
    private StorePreferenceOutputQueue queue;

    @StreamListener(StorePreferenceOutputQueue.STORE_PREFERENCE_REQUEST_INPUT_QUEUE)
    public void storePreferenceRequest(StorePreferenceSearchRequestData request){
        StorePreferenceEntity preference = storePreferenceRepository.findOne(request.getStoreId());
        queue.responseOutput().send(
                MessageBuilder.withPayload(StorePreferenceSearchResponseData
                        .builder()
                        .ticker(request.getTicker())
                        .storePreference(preference != null ? preference.toData() : null)
                        .build()
                ).build()
        );
    }
}
