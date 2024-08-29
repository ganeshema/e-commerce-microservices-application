package com.ganeshgc.ecommerce.service;

import com.ganeshgc.ecommerce.customer.Customer;
import com.ganeshgc.ecommerce.customer.CustomerRequest;
import com.ganeshgc.ecommerce.customer.CustomerResponse;
import com.ganeshgc.ecommerce.exception.CustomerNotFoundException;
import com.ganeshgc.ecommerce.repository.CustomerRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public String createCustomer(CustomerRequest request) {
        var customer=customerRepository.save(customerMapper.toCustomer(request));
        return customer.getId();
    }

    public void updateCustomer(String id, CustomerRequest request) {
        var customer=customerRepository.findById(id).orElseThrow(()->new CustomerNotFoundException(
                format("Customer %s not found", id)
        ));

        mergerCustomer(customer,request);
        customerRepository.save(customer);
    }

    private void mergerCustomer(Customer customer, CustomerRequest request) {
        if(StringUtils.isNotBlank(request.firstName())){
            customer.setFirstName(request.firstName());
        }
        if(StringUtils.isNotBlank(request.lastName())){
            customer.setLastName(request.lastName());
        }
        if(StringUtils.isNotBlank(request.email())){
            customer.setEmail(request.email());
        }
        if(request.address()!=null){
            customer.setAddress(request.address());
        }
    }

    public List<CustomerResponse> findAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public Boolean existById(String customerId) {
        return customerRepository.findById(customerId).isPresent();

    }

    public CustomerResponse findById(String customerId) {
        return customerRepository.findById(customerId)
                .map(customer -> customerMapper.fromCustomer(customer))
                .orElseThrow(()-> new CustomerNotFoundException(
                        format("No customer found with the provided id :: %s" , customerId)));
    }

    public void deleteById(String customerId) {
       var customer= customerRepository.findById(customerId).orElseThrow(()->
               new CustomerNotFoundException("No customer found with the provided id :: " + customerId));
       customerRepository.delete(customer);
    }
}