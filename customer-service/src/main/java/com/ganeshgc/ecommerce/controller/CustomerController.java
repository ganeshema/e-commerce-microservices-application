package com.ganeshgc.ecommerce.controller;

import com.ganeshgc.ecommerce.customer.CustomerRequest;
import com.ganeshgc.ecommerce.customer.CustomerResponse;
import com.ganeshgc.ecommerce.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<String> createCustomer(@RequestBody @Valid CustomerRequest request){

        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCustomer(@PathVariable String id, @RequestBody @Valid CustomerRequest request){
        customerService.updateCustomer(id, request);
        return ResponseEntity.accepted().build();
    }
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> findAll(){
        return ResponseEntity.ok(customerService.findAllCustomers());
    }
    @GetMapping("/exist/{customer-id}")
    public ResponseEntity<Boolean> existById(@PathVariable("customer-id") String customerId){
        return ResponseEntity.ok(customerService.existById(customerId));
    }
    @GetMapping("/{customer-id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable("customer-id") String customerId){
        return ResponseEntity.ok(customerService.findById(customerId));
    }
    @DeleteMapping("/{customer-id}")
    public ResponseEntity<Void> deleteById(@PathVariable("customer-id") String customerId){
        customerService.deleteById(customerId);
        return ResponseEntity.accepted().build();
    }
}
