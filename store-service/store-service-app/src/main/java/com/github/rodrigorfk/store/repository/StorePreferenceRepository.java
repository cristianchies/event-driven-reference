package com.github.rodrigorfk.store.repository;

import com.github.rodrigorfk.store.data.StorePreferenceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@RepositoryRestResource(collectionResourceRel = "storePreference", path = "storePreference")
public interface StorePreferenceRepository extends MongoRepository<StorePreferenceEntity, Long> {
}
