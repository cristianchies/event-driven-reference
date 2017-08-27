package com.github.rodrigorfk.product.repository;

import com.github.rodrigorfk.product.data.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@RepositoryRestResource(collectionResourceRel = "product", path = "product")
public interface ProductRepository extends MongoRepository<ProductEntity, Long> {
}
