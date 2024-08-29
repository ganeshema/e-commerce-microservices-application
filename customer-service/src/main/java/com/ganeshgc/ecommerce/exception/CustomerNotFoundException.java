package com.ganeshgc.ecommerce.exception;



import lombok.Data;
import lombok.EqualsAndHashCode;

// The @EqualsAndHashCode annotation automatically generates the equals() and hashCode() methods for this class.
// The callSuper = true parameter indicates that the generated methods should also include fields from the superclass (RuntimeException).
@EqualsAndHashCode(callSuper = true)
// The @Data annotation from Lombok automatically generates getters, setters, toString(), equals(), and hashCode() methods for all fields in the class.
// Since this class extends RuntimeException, the equals() and hashCode() methods include fields from RuntimeException as well.
@Data
public class CustomerNotFoundException extends RuntimeException {

    // This field holds a custom error message that will be passed to the exception when it is thrown.
    private final String msg;
    // The @Data annotation will generate a constructor for this field.
    // Since the msg field is declared as final, it must be initialized either in the constructor or inline.
}
