package com.ganeshgc.ecommerce.service;

import com.ganeshgc.ecommerce.exception.ProductPurchaseException;
import com.ganeshgc.ecommerce.product.ProductPurchaseRequest;
import com.ganeshgc.ecommerce.product.ProductPurchaseResponse;
import com.ganeshgc.ecommerce.product.ProductRequest;
import com.ganeshgc.ecommerce.product.ProductResponse;
import com.ganeshgc.ecommerce.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Integer createProduct(
            ProductRequest request
    ) {
        var product = mapper.toProduct(request);
        return repository.save(product).getId();
    }

    public ProductResponse findById(Integer id) {
        return repository.findById(id)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID:: " + id));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toProductResponse)
                .collect(Collectors.toList());
    }

    // The @Transactional annotation indicates that the method is transactional.
// If any exception of type ProductPurchaseException occurs, the transaction will be rolled back,
// meaning any changes made to the database during this method will be undone.
    @Transactional(rollbackFor = ProductPurchaseException.class)
    public List<ProductPurchaseResponse> purchaseProducts(
            List<ProductPurchaseRequest> request
    ) {
        // Extract product IDs from the incoming list of purchase requests.
        // The request contains details about the products to be purchased,
        // and we need to get the product IDs to fetch them from the database.
        var productIds = request
                .stream()
                .map(ProductPurchaseRequest::productId) // Extract productId from each ProductPurchaseRequest
                .toList(); // Collect the IDs into a list

        // Fetch the list of products from the database that match the given IDs,
        // ordered by their ID. This ensures that we have all the products that the user wants to purchase.
        var storedProducts = repository.findAllByIdInOrderById(productIds);

        // Check if the number of requested products matches the number of products
        // retrieved from the database. If they don't match, it means one or more products
        // in the request don't exist in the database, and we throw an exception.
        if (productIds.size() != storedProducts.size()) {
            throw new ProductPurchaseException("One or more products does not exist");
        }

        // Sort the incoming purchase requests by product ID.
        // This is necessary to match each purchase request with the correct product
        // in the storedProducts list, which was also fetched in order by ID.
        var sortedRequest = request
                .stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId)) // Sort by productId
                .toList(); // Collect the sorted requests into a list

        // Create a list to hold the response objects that will be returned to the caller.
        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();

        // Iterate over the stored products and the sorted purchase requests simultaneously.
        // Since both lists are ordered by product ID, we can safely process them together.
        for (int i = 0; i < storedProducts.size(); i++) {
            var product = storedProducts.get(i); // Get the stored product from the database
            var productRequest = sortedRequest.get(i); // Get the corresponding purchase request

            // Check if the available quantity of the product is sufficient to fulfill the request.
            // If not, throw an exception to prevent the purchase and rollback the transaction.
            if (product.getAvailableQuantity() < productRequest.quantity()) {
                throw new ProductPurchaseException("Insufficient stock quantity for product with ID:: " + productRequest.productId());
            }

            // Update the available quantity of the product by subtracting the quantity requested.
            var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity); // Set the new available quantity

            // Save the updated product back to the database to persist the new quantity.
            repository.save(product);

            // Create a response object for the purchased product, mapping the product and
            // the quantity purchased, and add it to the list of purchased products.
            purchasedProducts.add(mapper.toProductPurchaseResponse(product, productRequest.quantity()));
        }

        // Return the list of purchase responses, each containing details about the purchased products.
        return purchasedProducts;
    }


}
