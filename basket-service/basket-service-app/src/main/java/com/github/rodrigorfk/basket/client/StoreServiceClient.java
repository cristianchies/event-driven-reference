package com.github.rodrigorfk.basket.client;

import com.github.rodrigorfk.store.commons.data.StorePreferenceData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@FeignClient(name = "store-service", url = "${service.store.url}", decode404 = true)
public interface StoreServiceClient {

    @RequestMapping(method = RequestMethod.GET, path = "/storePreference/{id}")
    public Resource<StorePreferenceData> getStorePreference(@PathVariable("id") Long storeId);
}
