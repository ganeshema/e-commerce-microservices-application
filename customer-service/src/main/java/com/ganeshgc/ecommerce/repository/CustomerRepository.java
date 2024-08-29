package com.ganeshgc.ecommerce.repository;

import com.ganeshgc.ecommerce.customer.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer,String> {
}