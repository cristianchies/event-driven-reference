package org.github.rodrigorfk.customer.repostiory;

import org.github.rodrigorfk.customer.data.CustomerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@Repository
public interface CustomerRepository extends MongoRepository<CustomerEntity,String>{
}
