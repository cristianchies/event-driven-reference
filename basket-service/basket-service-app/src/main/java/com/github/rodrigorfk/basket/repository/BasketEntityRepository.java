package com.github.rodrigorfk.basket.repository;

import com.github.rodrigorfk.basket.data.BasketEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public interface BasketEntityRepository extends MongoRepository<BasketEntity, String> {
}
